import core.domain.Coach;
import core.domain.Player;
import core.domain.TrainingPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CoachTrainingTest {

    private Coach coach;
    private Player healthyPlayer;
    private Player injuredPlayer;

    @BeforeEach
    void setUp() {
        coach = new Coach("Coach Smith", "passing");
        coach.addTrainingPlan(new TrainingPlan("passing", 5));

        healthyPlayer = new Player("Fit Player", "Midfielder", 22, 7);
        healthyPlayer.setAttribute("passing", 60);

        injuredPlayer = new Player("Hurt Player", "Midfielder", 22, 8);
        injuredPlayer.setAttribute("passing", 60);
        injuredPlayer.applyInjury(3);
    }

    @Test
    void trainPlayers_healthyPlayer_attributeIncreases() {
        int before = healthyPlayer.getAttribute("passing");
        coach.trainPlayers(java.util.List.of(healthyPlayer));
        assertTrue(healthyPlayer.getAttribute("passing") > before);
    }

    @Test
    void trainPlayers_injuredPlayer_attributeUnchanged() {
        int before = injuredPlayer.getAttribute("passing");
        coach.trainPlayers(java.util.List.of(injuredPlayer));
        assertEquals(before, injuredPlayer.getAttribute("passing"));
    }

    @Test
    void trainPlayers_differentSpecialty_noEffect() {
        Player p = new Player("P", "Defender", 25, 5);
        p.setAttribute("finishing", 50);
        // coach specialty is "passing", plan targets "passing" — "finishing" unchanged
        int before = p.getAttribute("finishing");
        coach.trainPlayers(java.util.List.of(p));
        assertEquals(before, p.getAttribute("finishing"));
    }
}
