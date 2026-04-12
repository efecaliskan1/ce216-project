import core.domain.Player;
import core.domain.Substitution;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubstitutionTest {

    @Test
    void substitutionStoresPlayerOut() {
        Player out = new Player("Ali", "CM", 25, 8);
        Player in  = new Player("Veli", "CM", 22, 14);
        Substitution sub = new Substitution(out, in, 2);
        assertEquals(out, sub.getPlayerOut());
    }

    @Test
    void substitutionStoresPlayerIn() {
        Player out = new Player("Ali", "CM", 25, 8);
        Player in  = new Player("Veli", "CM", 22, 14);
        Substitution sub = new Substitution(out, in, 2);
        assertEquals(in, sub.getPlayerIn());
    }

    @Test
    void substitutionStoresPeriod() {
        Player out = new Player("Ali", "CM", 25, 8);
        Player in  = new Player("Veli", "CM", 22, 14);
        Substitution sub = new Substitution(out, in, 1);
        assertEquals(1, sub.getPeriod());
    }
}
