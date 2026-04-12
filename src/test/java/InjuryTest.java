import core.domain.Injury;
import core.domain.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InjuryTest {

    @Test
    void injuryStoresPlayerCorrectly() {
        Player p = new Player("Ali", "ST", 22, 9);
        Injury injury = new Injury(p, 3, "hamstring");
        assertEquals(p, injury.getPlayer());
    }

    @Test
    void injuryStoresGamesOut() {
        Player p = new Player("Veli", "CB", 28, 4);
        Injury injury = new Injury(p, 5, "ankle sprain");
        assertEquals(5, injury.getGamesOut());
    }

    @Test
    void injuryStoresCause() {
        Player p = new Player("Can", "GK", 30, 1);
        Injury injury = new Injury(p, 2, "collision");
        assertEquals("collision", injury.getCause());
    }
}
