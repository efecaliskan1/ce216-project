import core.domain.MatchResult;
import core.domain.StandingEntry;
import core.domain.Team;
import core.services.StandingsService;
import sports.football.FootballStandingsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StandingsServiceTest {

    private StandingsService service;
    private FootballStandingsCalculator calc;
    private Team teamA, teamB;

    @BeforeEach
    void setUp() {
        calc    = new FootballStandingsCalculator();
        service = new StandingsService(calc);
        teamA   = new Team("Team A", "a.png", RosterRules.defaults());
        teamB   = new Team("Team B", "b.png", RosterRules.defaults());
    }

    @Test
    void processResult_delegatesToCalculator() {
        MatchResult mr = new MatchResult(2, 1, teamA, teamB, List.of(), 0);
        service.processResult(mr);
        // calculator should now have entries
        List<StandingEntry> table = service.getTable();
        assertFalse(table.isEmpty());
    }

    @Test
    void getTable_returnsStandingsSortedByPoints() {
        service.processResult(new MatchResult(1, 0, teamA, teamB, List.of(), 0));
        service.processResult(new MatchResult(2, 0, teamA, teamB, List.of(), 1));
        List<StandingEntry> table = service.getTable();
        assertEquals(teamA, table.get(0).getTeam());
        assertEquals(6, table.get(0).getPoints());
    }

    @Test
    void getTable_emptyInitially() {
        List<StandingEntry> table = service.getTable();
        assertTrue(table.isEmpty());
    }

    @Test
    void getCalculator_returnsSameInstance() {
        assertSame(calc, service.getCalculator());
    }
}
