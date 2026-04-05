import core.domain.MatchResult;
import core.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MatchResultTest {

    private Team home, away;

    @BeforeEach
    void setUp() {
        RosterRules r = RosterRules.defaults();
        home = new Team("Home FC", "h.png", r);
        away = new Team("Away FC", "a.png", r);
    }

    @Test
    void homeWin_getWinner_returnsHome() {
        MatchResult mr = new MatchResult(2, 0, home, away, List.of(), 1);
        assertTrue(mr.getWinner().isPresent());
        assertEquals(home, mr.getWinner().get());
    }

    @Test
    void awayWin_getWinner_returnsAway() {
        MatchResult mr = new MatchResult(0, 1, home, away, List.of(), 1);
        assertEquals(away, mr.getWinner().get());
    }

    @Test
    void draw_getWinner_isEmpty() {
        MatchResult mr = new MatchResult(1, 1, home, away, List.of(), 1);
        assertTrue(mr.getWinner().isEmpty());
        assertTrue(mr.isDraw());
    }

    @Test
    void getGoalsFor_returnsCorrectValue() {
        MatchResult mr = new MatchResult(3, 1, home, away, List.of(), 1);
        assertEquals(3, mr.getGoalsFor(home));
        assertEquals(1, mr.getGoalsFor(away));
    }

    @Test
    void getGoalsAgainst_returnsCorrectValue() {
        MatchResult mr = new MatchResult(3, 1, home, away, List.of(), 1);
        assertEquals(1, mr.getGoalsAgainst(home));
        assertEquals(3, mr.getGoalsAgainst(away));
    }
}
