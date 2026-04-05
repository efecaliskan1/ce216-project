import core.domain.Player;
import core.domain.TrainingPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    private Player player;

    @BeforeEach
    void setUp() { player = new Player("Ali Yılmaz", "Midfielder", 25, 10); }

    @Test
    void applyInjury_setsUnavailableAndCorrectGames() {
        player.applyInjury(3);
        assertFalse(player.isAvailable());
        assertEquals(3, player.getInjuredGamesRemaining());
    }

    @Test
    void decrementInjury_twice_stillInjured() {
        player.applyInjury(3);
        player.decrementInjury();
        player.decrementInjury();
        assertFalse(player.isAvailable());
        assertEquals(1, player.getInjuredGamesRemaining());
    }

    @Test
    void decrementInjury_fullyRecovers_afterAllGames() {
        player.applyInjury(3);
        player.decrementInjury();
        player.decrementInjury();
        player.decrementInjury();
        assertTrue(player.isAvailable());
        assertEquals(0, player.getInjuredGamesRemaining());
    }

    @Test
    void decrementInjury_atZero_doesNotThrow() {
        player.applyInjury(1);
        player.decrementInjury();
        assertDoesNotThrow(() -> player.decrementInjury());
        assertTrue(player.isAvailable());
    }

    @Test
    void train_increasesAttribute_cappedAt100() {
        player.setAttribute("passing", 98);
        TrainingPlan plan = new TrainingPlan("passing", 5);
        player.train(plan);
        assertEquals(100, player.getAttribute("passing"));
    }

    @Test
    void fatigue_increasesAndDecreases_withinBounds() {
        player.increaseFatigue(80);
        player.increaseFatigue(50); // would be 130, capped at 100
        assertEquals(100, player.getFatigueLevel());
        player.recoverFatigue(200); // would be -100, capped at 0
        assertEquals(0, player.getFatigueLevel());
    }
}
