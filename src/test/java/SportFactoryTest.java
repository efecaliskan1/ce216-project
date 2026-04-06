import core.app.SportFactory;
import interfaces.ISport;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SportFactoryTest {

    @Test
    void create_football_returnsFootballSport() {
        ISport sport = SportFactory.create("football");
        assertEquals("Football", sport.getName());
    }

    @Test
    void create_volleyball_returnsVolleyballSport() {
        ISport sport = SportFactory.create("volleyball");
        assertEquals("Volleyball", sport.getName());
    }

    @Test
    void create_caseInsensitive_works() {
        assertDoesNotThrow(() -> SportFactory.create("FOOTBALL"));
        assertDoesNotThrow(() -> SportFactory.create("Football"));
    }

    @Test
    void create_unknownSport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SportFactory.create("basketball"));
    }

    @Test
    void isRegistered_football_returnsTrue() {
        assertTrue(SportFactory.isRegistered("football"));
    }

    @Test
    void isRegistered_unknownSport_returnsFalse() {
        assertFalse(SportFactory.isRegistered("cricket"));
    }
}
