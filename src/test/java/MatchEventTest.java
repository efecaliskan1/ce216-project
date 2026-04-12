import core.domain.*;
import valueobjects.RosterRules;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MatchEventTest {

    @Test
    void eventStoresType() {
        Player p = new Player("Ali", "ST", 24, 9);
        Team t = new Team("TeamA", "logo.png", RosterRules.defaults());
        MatchEvent event = new MatchEvent(EventType.GOAL, 45, p, t);
        assertEquals(EventType.GOAL, event.getType());
    }

    @Test
    void eventStoresMinute() {
        Player p = new Player("Can", "CB", 28, 5);
        Team t = new Team("TeamB", "logo.png", RosterRules.defaults());
        MatchEvent event = new MatchEvent(EventType.INJURY, 70, p, t);
        assertEquals(70, event.getMinute());
    }

    @Test
    void eventStoresPlayerAndTeam() {
        Player p = new Player("Ege", "LW", 21, 11);
        Team t = new Team("TeamC", "logo.png", RosterRules.defaults());
        MatchEvent event = new MatchEvent(EventType.YELLOW_CARD, 30, p, t);
        assertEquals(p, event.getPlayer());
        assertEquals(t, event.getTeam());
    }
}
