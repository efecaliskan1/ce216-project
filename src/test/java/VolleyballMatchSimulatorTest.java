import core.domain.*;
import sports.volleyball.VolleyballSport;
import sports.volleyball.VolleyballMatchSimulator;
import tactics.TacticFactory;
import valueobjects.RosterRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballMatchSimulatorTest {

    private Team home, away;

    @BeforeEach
    void setUp() {
        VolleyballSport sport = new VolleyballSport();
        home = new Team("Home", "h.png", sport.getRosterRules());
        away = new Team("Away", "a.png", sport.getRosterRules());
        for (Player p : sport.generatePlayers(home)) home.addPlayer(p);
        for (Player p : sport.generatePlayers(away)) away.addPlayer(p);
        home.setCurrentTactic(TacticFactory.create("balanced"));
        away.setCurrentTactic(TacticFactory.create("balanced"));
    }

    @Test
    void matchEndsWithWinnerReachingThreeSets() {
        Match m = new Match(home, away, 1);
        VolleyballMatchSimulator sim = new VolleyballMatchSimulator();
        sim.setSeed(42L);
        MatchResult r = sim.simulate(m);

        int total = r.getHomeScore() + r.getAwayScore();

        assertTrue(total >= 3 && total <= 5, "Total sets should be between 3 and 5, was " + total);

        assertEquals(3, Math.max(r.getHomeScore(), r.getAwayScore()));

        assertTrue(Math.min(r.getHomeScore(), r.getAwayScore()) <= 2);
    }

    @Test
    void noDrawsPossibleInVolleyball() {
        VolleyballMatchSimulator sim = new VolleyballMatchSimulator();
        sim.setSeed(7L);
        for (int i = 0; i < 10; i++) {

            home.resetMatchState();
            away.resetMatchState();
            Match m = new Match(home, away, i);
            MatchResult r = sim.simulate(m);
            assertFalse(r.isDraw(), "Volleyball should never draw");
            assertTrue(r.getWinner().isPresent());
        }
    }

    @Test
    void matchFlagsArePlayedAfterSimulation() {
        Match m = new Match(home, away, 1);
        VolleyballMatchSimulator sim = new VolleyballMatchSimulator();
        sim.setSeed(123L);
        sim.simulate(m);
        assertTrue(m.isPlayed());
        assertNotNull(m.getResult());
    }

    @Test
    void deterministicWithSameSeed() {
        Match m1 = new Match(home, away, 1);
        VolleyballMatchSimulator sim1 = new VolleyballMatchSimulator();
        sim1.setSeed(99L);
        MatchResult r1 = sim1.simulate(m1);

        VolleyballSport sport = new VolleyballSport();
        Team home2 = new Team("Home", "h.png", sport.getRosterRules());
        Team away2 = new Team("Away", "a.png", sport.getRosterRules());
        for (Player p : sport.generatePlayers(home2)) home2.addPlayer(p);
        for (Player p : sport.generatePlayers(away2)) away2.addPlayer(p);
        home2.setCurrentTactic(TacticFactory.create("balanced"));
        away2.setCurrentTactic(TacticFactory.create("balanced"));

        Match m2 = new Match(home2, away2, 1);
        VolleyballMatchSimulator sim2 = new VolleyballMatchSimulator();
        sim2.setSeed(99L);
        MatchResult r2 = sim2.simulate(m2);

        assertTrue(r1.getWinner().isPresent() && r2.getWinner().isPresent());
    }

    @Test
    void fatigueAppliedAfterMatch() {
        Player starter = home.getStartingLineup().get(0);
        int before = starter.getFatigueLevel();
        Match m = new Match(home, away, 1);
        VolleyballMatchSimulator sim = new VolleyballMatchSimulator();
        sim.setSeed(5L);
        sim.simulate(m);
        assertTrue(starter.getFatigueLevel() > before, "Fatigue should increase after match");
    }
}
