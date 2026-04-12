import core.domain.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TrainingEventTest {

    @Test
    void executeIncreasesPlayerAttribute() {
        Player p = new Player("Ali", "ST", 22, 9);
        p.setAttribute("shooting", 50);
        Coach c = new Coach("Hoca", "shooting");
        TrainingPlan plan = new TrainingPlan("shooting", 5);
        TrainingEvent event = new TrainingEvent(c, List.of(p), plan);
        event.execute();
        assertEquals(55, p.getAttribute("shooting"));
    }

    @Test
    void executeSkipsInjuredPlayers() {
        Player p = new Player("Veli", "CB", 28, 4);
        p.setAttribute("defense", 60);
        p.applyInjury(3);
        Coach c = new Coach("Hoca", "defense");
        TrainingPlan plan = new TrainingPlan("defense", 5);
        TrainingEvent event = new TrainingEvent(c, List.of(p), plan);
        event.execute();
        // Sakatlanan oyuncu antrenman yapamaz
        assertEquals(60, p.getAttribute("defense"));
    }

    @Test
    void resultMapTracksGains() {
        Player p = new Player("Can", "GK", 25, 1);
        p.setAttribute("reflexes", 40);
        Coach c = new Coach("Hoca", "reflexes");
        TrainingPlan plan = new TrainingPlan("reflexes", 7);
        TrainingEvent event = new TrainingEvent(c, List.of(p), plan);
        event.execute();
        assertEquals(7, event.getResult().get(p));
    }

    @Test
    void injuredPlayerNotInResultMap() {
        Player p = new Player("Efe", "RB", 23, 2);
        p.setAttribute("speed", 70);
        p.applyInjury(1);
        Coach c = new Coach("Hoca", "speed");
        TrainingPlan plan = new TrainingPlan("speed", 3);
        TrainingEvent event = new TrainingEvent(c, List.of(p), plan);
        event.execute();
        assertFalse(event.getResult().containsKey(p));
    }
}
