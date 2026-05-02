import core.domain.Player;
import core.domain.Team;
import tactics.BalancedStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;

import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TeamTest {

    private Team team;

    @BeforeEach
    void setUp() {
        team = new Team("Test FC", "test.png", RosterRules.defaults());
        team.setCurrentTactic(new BalancedStrategy());
        for (int i = 0; i < 18; i++)
            team.addPlayer(new Player("Player" + i, "Midfielder", 25, i + 1));
    }

    @Test
    void startingLineup_hasCorrectSize() {
        assertEquals(11, team.getStartingLineup().size());
    }

    @Test
    void bench_hasCorrectSize() {
        assertEquals(7, team.getBench().size());
    }

    @Test
    void makeSub_injuredPlayerIn_returnsFalse() {
        Player injured = team.getBench().get(0);
        injured.applyInjury(2);
        assertFalse(team.makeSub(team.getStartingLineup().get(0), injured));
    }

    @Test
    void makeSub_valid_incrementsCounter() {
        Player out = team.getStartingLineup().get(0);
        Player in  = team.getBench().get(0);
        assertTrue(team.makeSub(out, in));
        assertEquals(1, team.getSubstitutionsMadeThisMatch());
    }

    @Test
    void makeSub_exceedsLimit_returnsFalse() {
        for (int i = 0; i < 3; i++)
            team.makeSub(team.getStartingLineup().get(i), team.getBench().get(i));
        Player out = team.getStartingLineup().get(3);
        Player in  = team.getBench().get(0);
        assertFalse(team.makeSub(out, in));
    }

    @Test
    void resetMatchState_resetsSubCounter() {
        team.makeSub(team.getStartingLineup().get(0), team.getBench().get(0));
        team.resetMatchState();
        assertEquals(0, team.getSubstitutionsMadeThisMatch());
    }

    @Test
    void validateLineup_allHealthy_returnsTrue() {
        assertTrue(team.validateLineup());
    }

    @Test
    void validateLineup_startingPlayerInjured_returnsFalse() {
        team.getStartingLineup().get(0).applyInjury(2);
        assertFalse(team.validateLineup());
    }

    @Test
    void swapStarterWithBench_validSwap_updatesBothZones() {
        Player starter = team.getStartingLineup().get(0);
        Player bench = team.getBench().get(0);

        assertTrue(team.swapStarterWithBench(starter, bench));
        assertTrue(team.getStartingLineup().contains(bench));
        assertTrue(team.getBench().contains(starter));
    }

    @Test
    void swapStarterWithBench_requiresHealthyBenchPlayer() {
        Player starter = team.getStartingLineup().get(0);
        Player bench = team.getBench().get(0);
        bench.applyInjury(2);

        assertFalse(team.swapStarterWithBench(starter, bench));
        assertTrue(team.getStartingLineup().contains(starter));
        assertTrue(team.getBench().contains(bench));
    }

    @Test
    void findAvailableBenchReplacementFor_prefersSamePosition() {
        Player starter = team.getStartingLineup().get(0);
        Player samePositionBench = team.getBench().get(0);
        samePositionBench = new Player("Replacement", starter.getPosition(), 24, 99);
        team.getPlayers().set(team.getRosterRules().getStartingLineupSize(), samePositionBench);

        assertEquals(samePositionBench, team.findAvailableBenchReplacementFor(starter));
    }

    @Test
    void organizeFootballLineup_reordersStartersToOneFourFourTwo() {
        Team footballTeam = new Team("Football FC", "football.png", new RosterRules(25, 14, 3));
        for (int i = 0; i < 3; i++) footballTeam.addPlayer(new Player("GK" + i, "Goalkeeper", 22, i + 1));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("DEF" + i, "Defender", 22, 10 + i));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("MID" + i, "Midfielder", 22, 20 + i));
        for (int i = 0; i < 6; i++) footballTeam.addPlayer(new Player("ATT" + i, "Striker", 22, 30 + i));

        footballTeam.organizeFootballLineup();

        Map<String, Long> starterCounts = footballTeam.getStartingLineup().stream()
                .collect(Collectors.groupingBy(Player::getPosition, Collectors.counting()));

        assertEquals(1L, starterCounts.getOrDefault("Goalkeeper", 0L));
        assertEquals(4L, starterCounts.getOrDefault("Defender", 0L));
        assertEquals(4L, starterCounts.getOrDefault("Midfielder", 0L));
        assertEquals(2L, starterCounts.getOrDefault("Striker", 0L));
        assertTrue(footballTeam.validateLineup());
    }

    @Test
    void swapStarterWithBench_forFootball_requiresSamePosition() {
        Team footballTeam = new Team("Football FC", "football.png", new RosterRules(25, 14, 3));
        for (int i = 0; i < 3; i++) footballTeam.addPlayer(new Player("GK" + i, "Goalkeeper", 22, i + 1));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("DEF" + i, "Defender", 22, 10 + i));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("MID" + i, "Midfielder", 22, 20 + i));
        for (int i = 0; i < 6; i++) footballTeam.addPlayer(new Player("ATT" + i, "Striker", 22, 30 + i));
        footballTeam.organizeFootballLineup();

        Player goalkeeperStarter = footballTeam.getStartingLineup().stream()
                .filter(p -> p.getPosition().equals("Goalkeeper"))
                .findFirst()
                .orElseThrow();
        Player defenderBench = footballTeam.getBench().stream()
                .filter(p -> p.getPosition().equals("Defender"))
                .findFirst()
                .orElseThrow();

        assertFalse(footballTeam.swapStarterWithBench(goalkeeperStarter, defenderBench));
    }

    @Test
    void autoReplaceInjuredStarters_swapsWithHealthySamePositionBench() {
        Team footballTeam = new Team("Football FC", "football.png", new RosterRules(25, 14, 3));
        for (int i = 0; i < 3; i++) footballTeam.addPlayer(new Player("GK" + i, "Goalkeeper", 22, i + 1));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("DEF" + i, "Defender", 22, 10 + i));
        for (int i = 0; i < 8; i++) footballTeam.addPlayer(new Player("MID" + i, "Midfielder", 22, 20 + i));
        for (int i = 0; i < 6; i++) footballTeam.addPlayer(new Player("ATT" + i, "Striker", 22, 30 + i));
        footballTeam.organizeFootballLineup();

        Player injuredStarter = footballTeam.getStartingLineup().stream()
                .filter(p -> p.getPosition().equals("Defender"))
                .findFirst()
                .orElseThrow();
        injuredStarter.applyInjury(2);

        Player healthyBenchDefender = footballTeam.getBench().stream()
                .filter(p -> p.getPosition().equals("Defender") && !p.isInjured())
                .findFirst()
                .orElseThrow();

        assertTrue(footballTeam.autoReplaceInjuredStarters());
        assertTrue(footballTeam.getStartingLineup().contains(healthyBenchDefender));
        assertTrue(footballTeam.getBench().contains(injuredStarter));
        assertTrue(footballTeam.validateLineup());
    }
}
