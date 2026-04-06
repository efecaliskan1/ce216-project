import core.domain.*;
import observer.UIMatchObserver;
import sports.football.FootballMatchSimulator;
import tactics.BalancedStrategy;
import tactics.DefensiveStrategy;
import tactics.HighPressStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;
import static org.junit.jupiter.api.Assertions.*;

public class FootballMatchSimulatorTest {

    private FootballMatchSimulator sim;

    @BeforeEach
    void setUp() { sim = new FootballMatchSimulator(); }

    @Test
    void sameSeed_producesSameScore() {
        sim.setSeed(42);
        MatchResult r1 = sim.simulate(buildMatch(new BalancedStrategy(), new BalancedStrategy(), 1));
        sim.setSeed(42);
        MatchResult r2 = sim.simulate(buildMatch(new BalancedStrategy(), new BalancedStrategy(), 1));
        assertEquals(r1.getHomeScore(), r2.getHomeScore());
        assertEquals(r1.getAwayScore(), r2.getAwayScore());
    }

    @Test
    void scores_areNonNegative() {
        sim.setSeed(7);
        MatchResult r = sim.simulate(buildMatch(new BalancedStrategy(), new BalancedStrategy(), 1));
        assertTrue(r.getHomeScore() >= 0 && r.getAwayScore() >= 0);
    }

    @Test
    void observer_receivesTwoPeriodEndEvents() {
        int[] count = {0};
        UIMatchObserver obs = new UIMatchObserver();
        obs.setOnPeriodEnd((p, h, a) -> count[0]++);
        sim.setObserver(obs);
        sim.setSeed(1);
        sim.simulate(buildMatch(new BalancedStrategy(), new BalancedStrategy(), 1));
        assertEquals(2, count[0]);
    }

    @Test
    void highPress_causeMoreFatigueThanDefensive() {
        Team hpTeam  = buildTeam("HP",  new HighPressStrategy());
        Team defTeam = buildTeam("DEF", new DefensiveStrategy());
        Team opp1 = buildTeam("Opp1", new BalancedStrategy());
        Team opp2 = buildTeam("Opp2", new BalancedStrategy());

        int hpBefore  = hpTeam.getStartingLineup().get(0).getFatigueLevel();
        int defBefore = defTeam.getStartingLineup().get(0).getFatigueLevel();

        sim.setSeed(5); sim.simulate(new Match(hpTeam,  opp1, 1));
        sim.setSeed(5); sim.simulate(new Match(defTeam, opp2, 1));

        int hpGain  = hpTeam.getStartingLineup().get(0).getFatigueLevel()  - hpBefore;
        int defGain = defTeam.getStartingLineup().get(0).getFatigueLevel() - defBefore;
        assertTrue(hpGain > defGain);
    }

    @Test
    void matchIsMarkedPlayed_afterSimulation() {
        Match m = buildMatch(new BalancedStrategy(), new BalancedStrategy(), 1);
        sim.setSeed(3);
        sim.simulate(m);
        assertTrue(m.isPlayed());
        assertNotNull(m.getResult());
    }

    // ---- helpers ----
    private Match buildMatch(interfaces.ITacticStrategy homeTac, interfaces.ITacticStrategy awayTac, int week) {
        Team home = buildTeam("Home", homeTac);
        Team away = buildTeam("Away", awayTac);
        return new Match(home, away, week);
    }

    private Team buildTeam(String name, interfaces.ITacticStrategy tac) {
        Team t = new Team(name, "logo.png", RosterRules.defaults());
        for (int i = 0; i < 18; i++) {
            Player p = new Player("P" + i, i == 0 ? "Goalkeeper" : "Midfielder", 25, i + 1);
            for (String a : new String[]{"finishing","passing","tackling","dribbling","pace","stamina","strength"})
                p.setAttribute(a, 60);
            t.addPlayer(p);
        }
        t.setCurrentTactic(tac);
        return t;
    }
}
