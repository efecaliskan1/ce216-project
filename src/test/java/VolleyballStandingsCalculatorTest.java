import core.domain.*;
import sports.volleyball.VolleyballStandingsCalculator;
import valueobjects.RosterRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class VolleyballStandingsCalculatorTest {

    private Team teamA, teamB;

    @BeforeEach
    void setUp() {
        RosterRules r = new RosterRules(12, 6, 6);
        teamA = new Team("A", "a.png", r);
        teamB = new Team("B", "b.png", r);
    }

    private MatchResult sweep(Team winner, Team loser, int setsWon, int setsLost) {
        int home = winner.equals(teamA) ? setsWon : setsLost;
        int away = winner.equals(teamA) ? setsLost : setsWon;
        return new MatchResult(home, away, teamA, teamB, new ArrayList<>(), 1);
    }

    @Test
    void win3_0_gives3PointsTo0() {
        VolleyballStandingsCalculator calc = new VolleyballStandingsCalculator();
        calc.update(sweep(teamA, teamB, 3, 0));
        List<StandingEntry> table = calc.getStandings();
        assertEquals(3, table.get(0).getPoints());
        assertEquals(teamA, table.get(0).getTeam());
        assertEquals(0, table.get(1).getPoints());
    }

    @Test
    void win3_1_gives3PointsTo0() {
        VolleyballStandingsCalculator calc = new VolleyballStandingsCalculator();
        calc.update(sweep(teamA, teamB, 3, 1));
        List<StandingEntry> table = calc.getStandings();
        assertEquals(3, table.get(0).getPoints());
        assertEquals(0, table.get(1).getPoints());
    }

    @Test
    void win3_2_gives2PointsTo1() {
        VolleyballStandingsCalculator calc = new VolleyballStandingsCalculator();
        calc.update(sweep(teamA, teamB, 3, 2));
        List<StandingEntry> table = calc.getStandings();
        // winner is teamA (first in standings)
        assertEquals(2, table.get(0).getPoints());
        assertEquals(1, table.get(1).getPoints());
    }

    @Test
    void winsAndLossesTrackedCorrectly() {
        VolleyballStandingsCalculator calc = new VolleyballStandingsCalculator();
        calc.update(sweep(teamA, teamB, 3, 1));
        calc.update(sweep(teamA, teamB, 3, 0));
        List<StandingEntry> table = calc.getStandings();
        StandingEntry aEntry = table.stream()
                .filter(e -> e.getTeam().equals(teamA)).findFirst().orElseThrow();
        assertEquals(2, aEntry.getWon());
        assertEquals(0, aEntry.getLost());
        assertEquals(6, aEntry.getPoints());
    }

    @Test
    void noDrawsEverRegistered() {
        VolleyballStandingsCalculator calc = new VolleyballStandingsCalculator();
        calc.update(sweep(teamA, teamB, 3, 2));
        calc.update(sweep(teamB, teamA, 3, 0));
        for (StandingEntry e : calc.getStandings()) {
            assertEquals(0, e.getDrawn(), "Volleyball should never register a draw");
        }
    }
}
