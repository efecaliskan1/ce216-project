import interfaces.IMatchSimulator;
import interfaces.IStandingsCalculator;
import sports.volleyball.VolleyballSport;
import sports.volleyball.VolleyballMatchSimulator;
import sports.volleyball.VolleyballStandingsCalculator;
import core.domain.Player;
import core.domain.Team;
import valueobjects.RosterRules;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballSportTest {

    @Test
    void sportNameIsVolleyball() {
        VolleyballSport s = new VolleyballSport();
        assertEquals("Volleyball", s.getName());
    }

    @Test
    void matchPeriodsIsFive() {
        VolleyballSport s = new VolleyballSport();
        assertEquals(5, s.getMatchPeriods());
    }

    @Test
    void rosterRulesAre12_6_6() {
        VolleyballSport s = new VolleyballSport();
        RosterRules r = s.getRosterRules();
        assertEquals(12, r.getRosterSize());
        assertEquals(6, r.getBenchSize());
        assertEquals(6, r.getStartingLineupSize());
    }

    @Test
    void createsMatchSimulator() {
        VolleyballSport s = new VolleyballSport();
        IMatchSimulator sim = s.createMatchSimulator();
        assertInstanceOf(VolleyballMatchSimulator.class, sim);
    }

    @Test
    void createsStandingsCalculator() {
        VolleyballSport s = new VolleyballSport();
        IStandingsCalculator calc = s.createStandingsCalculator();
        assertInstanceOf(VolleyballStandingsCalculator.class, calc);
    }

    @Test
    void generates12PlayersWithAttributes() {
        VolleyballSport s = new VolleyballSport();
        Team t = new Team("X", "x.png", s.getRosterRules());
        List<Player> players = s.generatePlayers(t);
        assertEquals(12, players.size());
        for (Player p : players) {
            assertTrue(p.getAttribute("spiking")   >= 1);
            assertTrue(p.getAttribute("blocking")  >= 1);
            assertTrue(p.getAttribute("serving")   >= 1);
        }
    }
}
