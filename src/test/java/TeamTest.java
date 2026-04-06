import core.domain.Player;
import core.domain.Team;
import tactics.BalancedStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;
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
}
