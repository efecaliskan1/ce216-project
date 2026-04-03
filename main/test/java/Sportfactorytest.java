package core.app;

import interfaces.ISport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sports.football.FootballSport;
import sports.volleyball.VolleyballSport;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class SportFactoryTest {


    @BeforeEach
    void resetRegistry() {
       
        SportFactory.register("football",   FootballSport::new);
        SportFactory.register("volleyball", VolleyballSport::new);
    }

   

    @Test
    void create_football_returnsFootballSport() {
        ISport sport = SportFactory.create("football");
        assertInstanceOf(FootballSport.class, sport);
    }

    @Test
    void create_volleyball_returnsVolleyballSport() {
        ISport sport = SportFactory.create("volleyball");
        assertInstanceOf(VolleyballSport.class, sport);
    }

    @Test
    void create_unknownSport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> SportFactory.create("xyz"));
    }

    @Test
    void create_caseInsensitive_football() {
        ISport sport = SportFactory.create("FOOTBALL");
        assertInstanceOf(FootballSport.class, sport);
    }

    @Test
    void create_returnsNewInstanceEachTime() {
        ISport a = SportFactory.create("football");
        ISport b = SportFactory.create("football");
        assertNotSame(a, b);
    }

   

    @Test
    void register_newSport_appearsInAvailableSports() {
        Supplier<ISport> dummy = () -> new FootballSport(); 
        SportFactory.register("testsport", dummy);
        assertTrue(SportFactory.getAvailableSports().contains("testsport"));
    }

    @Test
    void register_newSport_canBeCreated() {
        SportFactory.register("testsport2", FootballSport::new);
        assertNotNull(SportFactory.create("testsport2"));
    }

    

    @Test
    void getAvailableSports_containsFootballAndVolleyball() {
        assertTrue(SportFactory.getAvailableSports().contains("football"));
        assertTrue(SportFactory.getAvailableSports().contains("volleyball"));
    }

    @Test
    void isRegistered_knownSport_true() {
        assertTrue(SportFactory.isRegistered("football"));
    }

    @Test
    void isRegistered_unknownSport_false() {
        assertFalse(SportFactory.isRegistered("cricket"));
    }
}