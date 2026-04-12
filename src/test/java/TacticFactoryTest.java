import tactics.*;
import interfaces.ITacticStrategy;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TacticFactoryTest {

    @Test
    void createsDefensiveStrategy() {
        ITacticStrategy tactic = TacticFactory.create("defensive");
        assertInstanceOf(DefensiveStrategy.class, tactic);
    }

    @Test
    void createsBalancedStrategy() {
        ITacticStrategy tactic = TacticFactory.create("balanced");
        assertInstanceOf(BalancedStrategy.class, tactic);
    }

    @Test
    void createsHighPressStrategy() {
        ITacticStrategy tactic = TacticFactory.create("highpress");
        assertInstanceOf(HighPressStrategy.class, tactic);
    }

    @Test
    void createsCounterAttackStrategy() {
        ITacticStrategy tactic = TacticFactory.create("counterattack");
        assertInstanceOf(CounterAttackStrategy.class, tactic);
    }

    @Test
    void unknownTacticThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> TacticFactory.create("tiki-taka"));
    }

    @Test
    void caseInsensitive() {
        ITacticStrategy tactic = TacticFactory.create("DEFENSIVE");
        assertInstanceOf(DefensiveStrategy.class, tactic);
    }
}
