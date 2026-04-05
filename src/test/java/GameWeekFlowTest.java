import core.domain.Coach;
import core.domain.GameWeek;
import core.domain.Match;
import core.domain.Player;
import core.domain.Team;
import core.domain.TrainingEvent;
import core.domain.TrainingPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameWeekFlowTest {

    private GameWeek gw;
    private Player healthyPlayer;
    private Player injuredPlayer;
    private Coach coach;

    @BeforeEach
    void setUp() {
        gw = new GameWeek(0);

        healthyPlayer = new Player("Fit Player", "Midfielder", 22, 7);
        healthyPlayer.setAttribute("passing", 50);

        injuredPlayer = new Player("Hurt Player", "Midfielder", 22, 8);
        injuredPlayer.setAttribute("passing", 50);
        injuredPlayer.applyInjury(3);

        coach = new Coach("Coach A", "passing");
        coach.addTrainingPlan(new TrainingPlan("passing", 5));

        TrainingEvent te = new TrainingEvent(
                coach,
                List.of(healthyPlayer, injuredPlayer),
                coach.getTrainingPlans().get(0)
        );
        gw.addTrainingEvent(te);
    }

    @Test
    void runTraining_healthyPlayer_attributeIncreases() {
        int before = healthyPlayer.getAttribute("passing");
        gw.runTraining();
        assertTrue(healthyPlayer.getAttribute("passing") > before);
    }

    @Test
    void runTraining_injuredPlayer_attributeUnchanged() {
        int before = injuredPlayer.getAttribute("passing");
        gw.runTraining();
        assertEquals(before, injuredPlayer.getAttribute("passing"));
    }

    @Test
    void markCompleted_setsFlag() {
        assertFalse(gw.isCompleted());
        gw.markCompleted();
        assertTrue(gw.isCompleted());
    }

    @Test
    void addFixture_fixtureAppearsInList() {
        valueobjects.RosterRules r = valueobjects.RosterRules.defaults();
        Team home = new Team("H", "h.png", r);
        Team away = new Team("A", "a.png", r);
        Match m = new Match(home, away, 0);
        gw.addFixture(m);
        assertEquals(1, gw.getFixtures().size());
        assertSame(m, gw.getFixtures().get(0));
    }

    @Test
    void getWeekNumber_returnsCorrectValue() {
        GameWeek gw5 = new GameWeek(5);
        assertEquals(5, gw5.getWeekNumber());
    }
}
