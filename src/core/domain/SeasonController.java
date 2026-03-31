package core.app;

import core.domain.*;
import core.services.FixtureGenerator;
import core.services.PlayerGenerator;
import core.services.StandingsService;
import interfaces.ISport;
import interfaces.IStandingsCalculator;
import interfaces.MatchObserver;
import tactics.TacticFactory;

import java.util.Arrays;
import java.util.List;

public class SeasonController {

    private Season          season;
    private ISport          sport;
    private MatchCoordinator matchCoordinator;
    private StandingsService standingsService;
    private MatchObserver   observer;

    private static final List<String> TEAM_NAMES = Arrays.asList(
        "Red Lions",    "Blue Eagles",   "Green Wolves",  "Black Panthers",
        "White Tigers", "Gold Stars",    "Silver Hawks",  "Bronze Bulls",
        "Purple Knights","Orange Foxes", "Yellow Falcons","Pink Dolphins",
        "Cyan Sharks",  "Crimson Bears", "Indigo Ravens", "Teal Dragons"
    );

    public void setObserver(MatchObserver obs) { this.observer = obs; }

    public void startSeason(String sportName) {
        this.sport = SportFactory.create(sportName);   // throws on unknown

        IStandingsCalculator calc = sport.createStandingsCalculator();
        standingsService  = new StandingsService(calc);
        matchCoordinator  = new MatchCoordinator(sport);
        if (observer != null) matchCoordinator.setObserver(observer);

        League league = new League(sport.getName() + " League");
        List<Team> teams = PlayerGenerator.generateTeams(sport, TEAM_NAMES);
        for (Team t : teams) {
            t.setCurrentTactic(TacticFactory.create("balanced"));
            league.addTeam(t);
        }

        FixtureGenerator fg = new FixtureGenerator();
        List<GameWeek> weeks = fg.generate(league);

        season = new Season(sport, league, 1);
        season.setCalculator(calc);
        for (GameWeek gw : weeks) season.addGameWeek(gw);
    }

    public void nextWeek() {
        if (season == null || season.isFinished()) return;
        GameWeek gw = season.getCurrentGameWeek();
        if (gw == null) return;

        gw.runTraining();

        // Order: executeMatch → applyNewInjuries → updateStandings → decrementInjuries
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

    public Season          getSeason()          { return season; }
    public ISport          getSport()           { return sport; }
    public StandingsService getStandingsService() { return standingsService; }
}
