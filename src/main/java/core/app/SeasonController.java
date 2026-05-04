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
        this.season = loaded;
        this.sport = loaded.getSport();
        this.matchCoordinator = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);
        this.standingsService = new StandingsService(loaded.getCalculator());
        this.userTeam = loaded.getUserTeam();
    }

    public void startSeason(String sportName) {
        this.sport = SportFactory.create(sportName);   // throws on unknown

        IStandingsCalculator calc = sport.createStandingsCalculator();
        standingsService  = new StandingsService(calc);
        matchCoordinator  = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);

        League league = new League(sport.getName() + " League");
        List<Team> teams = PlayerGenerator.generateTeams(sport, NameDataService.pickTeamNames(16, new Random()));
        for (Team t : teams) {
            t.setCurrentTactic(TacticFactory.create("balanced"));
            league.addTeam(t);
        }

        FixtureGenerator fg = new FixtureGenerator();
        List<GameWeek> weeks = fg.generate(league);

        // Register training events for each team in each game week
        for (GameWeek gw : weeks) {
            for (Team t : league.getTeams()) {
                if (t.getCoach() != null && !t.getCoach().getTrainingPlans().isEmpty()) {
                    TrainingPlan plan = t.getCoach().getTrainingPlans().get(0);
                    TrainingEvent te = new TrainingEvent(t.getCoach(), t.getPlayers(), plan);
                    gw.addTrainingEvent(te);
                }
            }
        }

        season = new Season(sport, league, 1);
        season.setCalculator(calc);
        for (GameWeek gw : weeks) season.addGameWeek(gw);
        userTeam = null;
    }

    public Team getUserTeam()           { return userTeam; }
    public void setUserTeam(Team team)  {
        this.userTeam = team;
        if (season != null) {
            season.setUserTeam(team);
        }
    }

    public void nextWeek() {
        if (season == null || season.isFinished()) return;
        GameWeek gw = season.getCurrentGameWeek();
        if (gw == null) return;

        // Phase 1: Training
        gw.runTraining();

        // Phase 2: Recover fatigue from previous week
        recoverFatigueForAll();

        // Phase 3: Play matches → apply new injuries → update standings → decrement injuries
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
    }

    public void simulateOtherMatches(Match userMatch) {
        if (season == null || userMatch == null) return;
        GameWeek gw = season.getCurrentGameWeek();
        if (gw == null) return;

        gw.runTraining();
        recoverFatigueForAll();
        for (Match match : gw.getFixtures()) {
            if (match == userMatch || match.isPlayed()) {
                continue;
            }
            MatchResult result = matchCoordinator.executeMatch(match);
            applyNewInjuries(result);
            standingsService.processResult(result);
            season.getLeague().addResult(result);
            decrementInjuries(match.getHomeTeam());
            decrementInjuries(match.getAwayTeam());
        }
    }

    public void finishWeekAfterUserMatch(Match userMatch) {
        if (season == null || userMatch == null) return;
        if (userMatch.isPlayed() && userMatch.getResult() != null) {
            applyNewInjuries(userMatch.getResult());
            season.getLeague().addResult(userMatch.getResult());
            decrementInjuries(userMatch.getHomeTeam());
            decrementInjuries(userMatch.getAwayTeam());
        }
        GameWeek gw = season.getCurrentGameWeek();
        if (gw != null) {
            gw.markCompleted();
        }
        season.advanceWeek();
    }

    private void applyNewInjuries(MatchResult result) {
        for (MatchEvent e : result.getEvents()) {
            if (e.getType() == EventType.INJURY) {
                Player p = e.getPlayer();
                if (!p.isInjured()) p.applyInjury(1 + (int)(Math.random() * 3));
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
