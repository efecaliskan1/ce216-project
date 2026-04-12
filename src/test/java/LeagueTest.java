import core.domain.*;
import valueobjects.RosterRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LeagueTest {

    private League league;
    private Team teamA, teamB;

    @BeforeEach
    void setUp() {
        league = new League("Super Lig");
        teamA = new Team("Fenerbahce", "fb.png", RosterRules.defaults());
        teamB = new Team("Galatasaray", "gs.png", RosterRules.defaults());
        league.addTeam(teamA);
        league.addTeam(teamB);
    }

    @Test
    void leagueNameCorrect() {
        assertEquals("Super Lig", league.getName());
    }

    @Test
    void addTeamIncreasesCount() {
        assertEquals(2, league.getTeams().size());
    }

    @Test
    void getFixturesForWeekFilters() {
        Match m1 = new Match(teamA, teamB, 1);
        Match m2 = new Match(teamB, teamA, 2);
        league.addFixture(m1);
        league.addFixture(m2);
        assertEquals(1, league.getFixturesForWeek(1).size());
        assertEquals(1, league.getFixturesForWeek(2).size());
    }

    @Test
    void getFixturesForTeamFilters() {
        Team teamC = new Team("Besiktas", "bjk.png", RosterRules.defaults());
        league.addTeam(teamC);
        Match m1 = new Match(teamA, teamB, 1);
        Match m2 = new Match(teamA, teamC, 2);
        Match m3 = new Match(teamB, teamC, 3);
        league.addFixture(m1);
        league.addFixture(m2);
        league.addFixture(m3);
        assertEquals(2, league.getFixturesForTeam(teamA).size());
        assertEquals(2, league.getFixturesForTeam(teamC).size());
    }

    @Test
    void matchHistoryStartsEmpty() {
        assertTrue(league.getMatchHistory().isEmpty());
    }
}
