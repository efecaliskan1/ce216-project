package abstracts;

import core.domain.*;
import interfaces.MatchObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.PeriodResult;
import valueobjects.TacticResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractMatchSimulatorTest {

   
    private static class TestSimulator extends AbstractMatchSimulator {
        @Override
        public MatchResult simulate(Match match) {
            return new MatchResult(match.getHomeTeam(), match.getAwayTeam(), 1, 0,
                    new ArrayList<>(), match.getWeekNumber());
        }
        @Override
        public PeriodResult simulatePeriod(Team home, Team away,
                                           TacticResult ht, TacticResult at,
                                           int period) {
            return new PeriodResult(1, 0, new ArrayList<>());
        }
    }

  
    private static class SpyObserver implements MatchObserver {
        final List<MatchEvent> goals         = new ArrayList<>();
        final List<MatchEvent> injuries      = new ArrayList<>();
        final List<MatchEvent> substitutions = new ArrayList<>();
        int periodEndCalls = 0;
        int matchEndCalls  = 0;

        @Override public void onGoal(MatchEvent e)         { goals.add(e); }
        @Override public void onInjury(MatchEvent e)       { injuries.add(e); }
        @Override public void onSubstitution(MatchEvent e) { substitutions.add(e); }
        @Override public void onPeriodEnd(int p, int h, int a) { periodEndCalls++; }
        @Override public void onMatchEnd(MatchResult r)    { matchEndCalls++; }
    }

    private TestSimulator sim;
    private Player        player;

    @BeforeEach
    void setUp() {
        sim    = new TestSimulator();
        player = new Player("Test Player", "MID", 22, 10);
    }

   

    @Test
    void setSeed_doesNotThrow() {
        assertDoesNotThrow(() -> sim.setSeed(42L));
    }

    @Test
    void setSeed_sameResults_withSameSeed() {
      
        sim.setSeed(99L);
        boolean r1 = sim.rollInjury(player);
        sim.setSeed(99L);
        boolean r2 = sim.rollInjury(player);
        assertEquals(r1, r2);
    }

   

    @Test
    void fireEvent_nullObserver_doesNotThrow() {
      
        Team team = makeTeam("Home");
        MatchEvent e = new MatchEvent(EventType.GOAL, 10, player, team);
        assertDoesNotThrow(() -> sim.fireEvent(e));
    }

    @Test
    void fireEvent_goal_callsOnGoal() {
        SpyObserver spy = new SpyObserver();
        sim.setObserver(spy);
        Team team = makeTeam("Home");
        MatchEvent e = new MatchEvent(EventType.GOAL, 10, player, team);
        sim.fireEvent(e);
        assertEquals(1, spy.goals.size());
    }

    @Test
    void fireEvent_injury_callsOnInjury() {
        SpyObserver spy = new SpyObserver();
        sim.setObserver(spy);
        Team team = makeTeam("Home");
        MatchEvent e = new MatchEvent(EventType.INJURY, 30, player, team);
        sim.fireEvent(e);
        assertEquals(1, spy.injuries.size());
    }

    @Test
    void fireEvent_substitution_callsOnSubstitution() {
        SpyObserver spy = new SpyObserver();
        sim.setObserver(spy);
        Team team = makeTeam("Home");
        MatchEvent e = new MatchEvent(EventType.SUBSTITUTION, 60, player, team);
        sim.fireEvent(e);
        assertEquals(1, spy.substitutions.size());
    }

   

    @Test
    void rollInjury_fatigue0_chanceIsApprox2Percent() {
        sim.setSeed(0L);
        player.recoverFatigue(100); 
        int injuries = 0;
        int trials   = 10_000;
        for (int i = 0; i < trials; i++) {
            if (sim.rollInjury(player)) injuries++;
        }
        double rate = (double) injuries / trials;
       
        assertTrue(rate >= 0.005 && rate <= 0.035,
                "Expected ~2% injury rate at fatigue=0, got: " + rate);
    }

    @Test
    void rollInjury_fatigue100_chanceIsApprox10Percent() {
        sim.setSeed(0L);
        player.increaseFatigue(100); 
        int injuries = 0;
        int trials   = 10_000;
        for (int i = 0; i < trials; i++) {
            if (sim.rollInjury(player)) injuries++;
        }
        double rate = (double) injuries / trials;
       
        assertTrue(rate >= 0.075 && rate <= 0.125,
                "Expected ~10% injury rate at fatigue=100, got: " + rate);
    }

    @Test
    void rollInjury_higherFatigue_higherInjuryRate() {
        sim.setSeed(42L);
        Player lowFatigue  = new Player("Low",  "DEF", 22, 1);
        Player highFatigue = new Player("High", "DEF", 22, 2);
        highFatigue.increaseFatigue(100);

        int lowCount = 0, highCount = 0;
        int trials = 5_000;
        for (int i = 0; i < trials; i++) {
            if (sim.rollInjury(lowFatigue))  lowCount++;
            if (sim.rollInjury(highFatigue)) highCount++;
        }
        assertTrue(highCount > lowCount,
                "Higher fatigue should produce more injuries");
    }

    

    private Team makeTeam(String name) {
        return new Team(name, "logo.png", new ArrayList<>(), null);
    }
}