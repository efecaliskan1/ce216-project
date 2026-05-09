package core.app;

import core.domain.*;
import core.services.FixtureGenerator;
import core.services.NameDataService;
import core.services.PlayerGenerator;
import core.services.StandingsService;
import interfaces.ISport;
import interfaces.IStandingsCalculator;
import interfaces.MatchObserver;
import tactics.TacticFactory;

import java.util.List;
import java.util.Random;

public class SeasonController {

    private Season          season;
    private ISport          sport;
    private MatchCoordinator matchCoordinator;
    private StandingsService standingsService;
    private MatchObserver   observer;
    private Team            userTeam;

    private static final int FATIGUE_RECOVERY_PER_WEEK = 15;

    public void setObserver(MatchObserver obs) { this.observer = obs; }

    public void loadExistingSeason(Season loaded) {
        this.season           = loaded;
        this.sport            = loaded.getSport();
        this.matchCoordinator = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);
        this.standingsService = new StandingsService(loaded.getCalculator());
        this.userTeam         = loaded.getUserTeam();
        PlayerGenerator.ensureOveralls(loaded.getLeague().getTeams());
    }

    public void startSeason(String sportName) {
        this.sport = SportFactory.create(sportName);

        IStandingsCalculator calc = sport.createStandingsCalculator();
        standingsService  = new StandingsService(calc);
        matchCoordinator  = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);

        League league = new League(sport.getName() + " League");
        List<String> teamNames = NameDataService.pickTeamNames(16, new Random());
        List<Team> teams = PlayerGenerator.generateTeams(sport, teamNames);
        for (Team t : teams) {
            t.setCurrentTactic(TacticFactory.create("balanced"));
            league.addTeam(t);
        }

        FixtureGenerator fg = new FixtureGenerator();
        List<GameWeek> weeks = fg.generate(league);

        season = new Season(league, sport, weeks, calc);
        userTeam = null;
    }

    public void startNextSeason() {
        if (season == null) return;

        for (Team team : season.getLeague().getTeams()) {
            team.resetMatchState();
            for (Player player : team.getPlayers()) {
                player.advanceSeasonAge();
                player.clearInjury();
                player.recoverFatigue(100);
            }
            team.organizeFootballLineup();
        }

        IStandingsCalculator calc = sport.createStandingsCalculator();
        standingsService  = new StandingsService(calc);
        matchCoordinator  = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);

        League league = new League(season.getLeague().getName());
        for (Team team : season.getLeague().getTeams()) {
            league.addTeam(team);
        }

        FixtureGenerator fg = new FixtureGenerator();
        List<GameWeek> weeks = fg.generate(league);

        season = new Season(league, sport, weeks, calc, season.getSeasonNumber() + 1);
        if (userTeam != null) season.setUserTeam(userTeam);
    }

    public Team getUserTeam()              { return userTeam; }
    public void setUserTeam(Team team)     {
        this.userTeam = team;
        if (season != null) season.setUserTeam(team);
    }

    public void nextWeek() {
        if (season.isFinished()) return;
        GameWeek gw = season.getCurrentGameWeek();

        gw.runTraining();

        recoverFatigueForAll();

        for (Match match : gw.getFixtures()) {
            if (!match.isPlayed()) {
                MatchResult result = matchCoordinator.executeMatch(match);
                applyNewInjuries(result);
                standingsService.processResult(result);
                season.getLeague().addResult(result);
                decrementInjuries(match.getHomeTeam());
                decrementInjuries(match.getAwayTeam());
            }
        }

        gw.markCompleted();
        season.advanceWeek();
        season.grantWeeklyTrainingSession();
    }

    public void simulateOtherMatches(Match userMatch) {
        GameWeek gw = season.getCurrentGameWeek();
        gw.runTraining();
        recoverFatigueForAll();
        for (Match m : gw.getFixtures()) {
            if (m == userMatch) continue;
            if (m.isPlayed()) continue;
            MatchResult result = matchCoordinator.executeMatch(m);
            applyNewInjuries(result);
            standingsService.processResult(result);
            season.getLeague().addResult(result);
            decrementInjuries(m.getHomeTeam());
            decrementInjuries(m.getAwayTeam());
        }
    }

    public void finishWeekAfterUserMatch(Match userMatch) {
        if (userMatch.isPlayed()) {
            applyNewInjuries(userMatch.getResult());

            season.getLeague().addResult(userMatch.getResult());
            decrementInjuries(userMatch.getHomeTeam());
            decrementInjuries(userMatch.getAwayTeam());
        }
        GameWeek gw = season.getCurrentGameWeek();
        gw.markCompleted();
        season.advanceWeek();
        season.grantWeeklyTrainingSession();
    }

    public int getAvailableTrainingSessions() {
        return season == null ? 0 : season.getAvailableTrainingSessions();
    }

    public boolean useTrainingSession(Team team) {
        if (season == null || team == null) return false;
        if (userTeam == null || team != userTeam) return false;
        if (team.getCoach() == null) return false;
        if (!season.consumeTrainingSession()) return false;
        team.getCoach().trainPlayers(team.getPlayers());
        return true;
    }

    private void applyNewInjuries(MatchResult result) {
        for (MatchEvent e : result.getEvents()) {
            if (e.getType() == EventType.INJURY) {
                Player p = e.getPlayer();
                if (!p.isInjured()) p.applyInjury(1 + (int)(Math.random() * 2));
            }
        }
    }

    private void decrementInjuries(Team team) {
        for (Player p : team.getPlayers()) {
            if (p.isInjured()) p.decrementInjury();
        }
    }

    private void recoverFatigueForAll() {
        for (Team t : season.getLeague().getTeams()) {
            for (Player p : t.getPlayers()) {
                p.recoverFatigue(FATIGUE_RECOVERY_PER_WEEK);
            }
        }
    }

    public Season          getSeason()          { return season; }
    public ISport          getSport()           { return sport; }
    public StandingsService getStandingsService() { return standingsService; }
    public MatchCoordinator getMatchCoordinator() { return matchCoordinator; }
}
