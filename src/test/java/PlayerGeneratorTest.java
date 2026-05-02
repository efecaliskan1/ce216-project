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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    void footballRoster_hasRequestedPositionDistribution() {
        List<Player> players = PlayerGenerator.generateForSport(sport, team);
        Map<String, Long> counts = players.stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));

        assertEquals(3L, counts.getOrDefault("Goalkeeper", 0L));
        assertEquals(8L, counts.getOrDefault("Defender", 0L));
        assertEquals(8L, counts.getOrDefault("Midfielder", 0L));
        assertEquals(6L, counts.getOrDefault("Striker", 0L));
    }

    @Test
    void generateTeams_buildsFootballLineupAsOneFourFourTwoAndBenchCoverage() {
        Team generated = PlayerGenerator.generateTeams(sport, List.of("Alpha FC")).get(0);

        Map<String, Long> starterCounts = generated.getStartingLineup().stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));
        Map<String, Long> benchCounts = generated.getBench().stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));

        assertEquals(1L, starterCounts.getOrDefault("Goalkeeper", 0L));
        assertEquals(4L, starterCounts.getOrDefault("Defender", 0L));
        assertEquals(4L, starterCounts.getOrDefault("Midfielder", 0L));
        assertEquals(2L, starterCounts.getOrDefault("Striker", 0L));

        assertTrue(benchCounts.getOrDefault("Goalkeeper", 0L) >= 2L);
        assertTrue(benchCounts.getOrDefault("Defender", 0L) >= 2L);
        assertTrue(benchCounts.getOrDefault("Midfielder", 0L) >= 2L);
        assertTrue(benchCounts.getOrDefault("Striker", 0L) >= 2L);
    }

    @Test
    void generatedPlayers_haveOverallBetween60And90() {
        List<Player> players = PlayerGenerator.generateForSport(sport, team);
        for (Player player : players) {
            assertTrue(player.getOverall() >= 60 && player.getOverall() <= 90);
        }
    }
}
