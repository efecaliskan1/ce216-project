import core.domain.Player;
import core.domain.Team;
import core.services.PlayerGenerator;
import interfaces.ISport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sports.football.FootballSport;
import valueobjects.RosterRules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerGeneratorTest {

    private ISport sport;
    private Team team;

    @BeforeEach
    void setUp() {
        sport = new FootballSport();
        team  = new Team("Test FC", "logo.png", sport.getRosterRules());
    }

    @Test
    void generateForSport_returnsCorrectRosterSize() {
        List<Player> players = PlayerGenerator.generateForSport(sport, team);
        assertEquals(sport.getRosterRules().getRosterSize(), players.size());
    }

    @Test
    void allAttributes_inRange1to100() {
        List<Player> players = PlayerGenerator.generateForSport(sport, team);
        for (Player p : players)
            for (int v : p.getAttributes().values())
                assertTrue(v >= 1 && v <= 100, "Attribute out of range: " + v);
    }

    @Test
    void shirtNumbers_noDuplicates() {
        List<Player> players = PlayerGenerator.generateForSport(sport, team);
        Set<Integer> nums = new HashSet<>();
        for (Player p : players)
            assertTrue(nums.add(p.getShirtNumber()), "Duplicate shirt: " + p.getShirtNumber());
    }

    @Test
    void generateAge_always17to35() {
        for (int i = 0; i < 200; i++) {
            int age = PlayerGenerator.generateAge();
            assertTrue(age >= 17 && age <= 35, "Age out of range: " + age);
        }
    }

    @Test
    void generateCoach_notNull_hasSpecialty() {
        var coach = PlayerGenerator.generateCoach();
        assertNotNull(coach);
        assertNotNull(coach.getSpecialty());
        assertFalse(coach.getSpecialty().isBlank());
    }
}
