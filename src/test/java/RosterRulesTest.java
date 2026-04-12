import valueobjects.RosterRules;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RosterRulesTest {

    @Test
    void defaultsCorrect() {
        RosterRules r = RosterRules.defaults();
        assertEquals(18, r.getRosterSize());
        assertEquals(7, r.getBenchSize());
        assertEquals(3, r.getSubstitutionLimitPerMatch());
    }

    @Test
    void startingLineupSizeCalculated() {
        RosterRules r = RosterRules.defaults();
        assertEquals(11, r.getStartingLineupSize());
    }

    @Test
    void customRulesWork() {
        RosterRules r = new RosterRules(20, 5, 5);
        assertEquals(15, r.getStartingLineupSize());
        assertEquals(5, r.getSubstitutionLimitPerMatch());
    }
}
