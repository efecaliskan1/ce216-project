import core.domain.Match;
import core.domain.Player;
import core.domain.Team;
import interfaces.ITacticStrategy;
import tactics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;
import static org.junit.jupiter.api.Assertions.*;

public class TacticTest {

    private Team team;
    private Match match;

    @BeforeEach
    void setUp() {
        RosterRules rules = RosterRules.defaults();
        team = new Team("Home", "logo.png", rules);
        Team away = new Team("Away", "logo.png", rules);
        for (int i = 0; i < 18; i++) {
            team.addPlayer(new Player("P" + i, "Midfielder", 25, i + 1));
            away.addPlayer(new Player("P" + i, "Midfielder", 25, i + 1));
        }
        team.setCurrentTactic(new BalancedStrategy());
        away.setCurrentTactic(new BalancedStrategy());
        match = new Match(team, away, 1);
    }

    @Test
    void highPress_fatigueMultiplier_is1_4() {
        assertEquals(1.4f, new HighPressStrategy().getFatigueMultiplier(), 0.001f);
    }

    @Test
    void defensive_fatigueMultiplier_is0_8() {
        assertEquals(0.8f, new DefensiveStrategy().getFatigueMultiplier(), 0.001f);
    }

    @Test
    void balanced_fatigueMultiplier_is1_0() {
        assertEquals(1.0f, new BalancedStrategy().getFatigueMultiplier(), 0.001f);
    }

    @Test
    void tacticFactory_unknownName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> TacticFactory.create("blitz_krieg"));
    }

    @Test
    void tacticFactory_allFourNames_returnNonNull() {
        assertNotNull(TacticFactory.create("defensive"));
        assertNotNull(TacticFactory.create("balanced"));
        assertNotNull(TacticFactory.create("highpress"));
        assertNotNull(TacticFactory.create("counterattack"));
    }

    @Test
    void applyCurrentTactics_homeAppliedTactic_notNull() {
        match.applyCurrentTactics();
        assertNotNull(match.homeAppliedTactic);
    }

    @Test
    void applyCurrentTactics_awayAppliedTactic_notNull() {
        match.applyCurrentTactics();
        assertNotNull(match.awayAppliedTactic);
    }

    @Test
    void defensiveStrategy_formation_is541() {
        ITacticStrategy t = new DefensiveStrategy();
        assertEquals("5-4-1", t.getFormation());
    }

    @Test
    void highPressStrategy_formation_is433() {
        ITacticStrategy t = new HighPressStrategy();
        assertEquals("4-3-3", t.getFormation());
    }
}
