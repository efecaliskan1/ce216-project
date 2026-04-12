import core.domain.*;
import valueobjects.PeriodResult;
import valueobjects.RosterRules;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PeriodResultTest {

    @Test
    void storesScoresCorrectly() {
        PeriodResult pr = new PeriodResult(2, 1, new ArrayList<>());
        assertEquals(2, pr.getHomeScore());
        assertEquals(1, pr.getAwayScore());
    }

    @Test
    void eventsListIsUnmodifiable() {
        PeriodResult pr = new PeriodResult(0, 0, new ArrayList<>());
        assertThrows(UnsupportedOperationException.class, () -> {
            Player p = new Player("X", "ST", 20, 9);
            Team t = new Team("T", "l.png", RosterRules.defaults());
            pr.getEvents().add(new MatchEvent(EventType.GOAL, 10, p, t));
        });
    }

    @Test
    void eventsListPreservesOriginal() {
        Player p = new Player("Ali", "ST", 22, 9);
        Team t = new Team("TeamA", "logo.png", RosterRules.defaults());
        List<MatchEvent> events = new ArrayList<>();
        events.add(new MatchEvent(EventType.GOAL, 15, p, t));
        PeriodResult pr = new PeriodResult(1, 0, events);
        assertEquals(1, pr.getEvents().size());
    }
}
