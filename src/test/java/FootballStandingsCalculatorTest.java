import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;
import sports.football.FootballStandingsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FootballStandingsCalculatorTest {

    private FootballStandingsCalculator calc;
    private Team teamA, teamB;

    @BeforeEach
    void setUp() {
        calc  = new FootballStandingsCalculator();
        teamA = new Team("Team A", "a.png", RosterRules.defaults());
        teamB = new Team("Team B", "b.png", RosterRules.defaults());
    }

    @Test
    void threeWins_ninePoints() {
        for (int i = 0; i < 3; i++)
            calc.update(new MatchResult(1, 0, teamA, teamB, List.of(), i));
        StandingEntry e = calc.getStandings().stream()
            .filter(s -> s.getTeam().equals(teamA)).findFirst().orElseThrow();
        assertEquals(9, e.getPoints());
    }

    @Test
    void oneWinTwoDraws_fivePoints() {
        calc.update(new MatchResult(2, 0, teamA, teamB, List.of(), 0));
        calc.update(new MatchResult(1, 1, teamA, teamB, List.of(), 1));
        calc.update(new MatchResult(0, 0, teamA, teamB, List.of(), 2));
        StandingEntry e = calc.getStandings().stream()
            .filter(s -> s.getTeam().equals(teamA)).findFirst().orElseThrow();
        assertEquals(5, e.getPoints());
    }

    @Test
    void higherPoints_ranksFirst() {
        calc.update(new MatchResult(1, 0, teamA, teamB, List.of(), 0));
        assertEquals(teamA, calc.getStandings().get(0).getTeam());
    }

    @Test
    void betterGoalDifference_ranksHigher_whenPointsEqual() {
        calc.update(new MatchResult(3, 0, teamA, teamB, List.of(), 0)); // A: 3pts GD+3
        calc.update(new MatchResult(1, 0, teamB, teamA, List.of(), 1)); // B: 3pts GD-2 → A still ahead
        assertTrue(calc.compareTeams(teamA, teamB) < 0);
    }

    @Test
    void seededConstructor_coinTossDeterministic() {
        FootballStandingsCalculator c1 = new FootballStandingsCalculator(42L);
        FootballStandingsCalculator c2 = new FootballStandingsCalculator(42L);
        Team t1 = new Team("T1", "t1.png", RosterRules.defaults());
        Team t2 = new Team("T2", "t2.png", RosterRules.defaults());
        assertEquals(c1.compareTeams(t1, t2), c2.compareTeams(t1, t2));
    }

    @Test
    void setSeed_resetsDeterminism() {
        FootballStandingsCalculator c = new FootballStandingsCalculator(99L);
        Team t1 = new Team("T1", "t1.png", RosterRules.defaults());
        Team t2 = new Team("T2", "t2.png", RosterRules.defaults());
        int first = c.compareTeams(t1, t2);
        c.setSeed(99L);
        assertEquals(first, c.compareTeams(t1, t2));
    }
}
