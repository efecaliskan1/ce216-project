import core.app.SeasonController;
import core.domain.Player;
import core.domain.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SeasonControllerTest {

    private SeasonController controller;

    @BeforeEach
    void setUp() { controller = new SeasonController(); }

    @Test
    void startSeason_football_seasonCreated() {
        controller.startSeason("football");
        assertNotNull(controller.getSeason());
    }

    @Test
    void startSeason_football_16TeamsCreated() {
        controller.startSeason("football");
        assertEquals(16, controller.getSeason().getLeague().getTeams().size());
    }

    @Test
    void startSeason_football_30GameWeeksCreated() {
        controller.startSeason("football");
        assertEquals(30, controller.getSeason().getGameWeeks().size());
    }

    @Test
    void startSeason_unknownSport_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> controller.startSeason("unknownsport"));
    }

    @Test
    void nextWeek_thrice_currentWeekIsThree() {
        controller.startSeason("football");
        controller.nextWeek();
        controller.nextWeek();
        controller.nextWeek();
        assertEquals(3, controller.getSeason().getCurrentWeek());
    }

    @Test
    void nextWeek_updatesStandings_tableNotEmpty() {
        controller.startSeason("football");
        controller.nextWeek();
        assertFalse(controller.getStandingsService().getTable().isEmpty());
    }

    @Test
    void nextWeek_injuredPlayer_gamesDecrementAfterMatch() {
        controller.startSeason("football");
        Team t = controller.getSeason().getLeague().getTeams().get(0);
        Player p = t.getPlayers().get(0);
        p.applyInjury(5);
        controller.nextWeek();
        assertTrue(p.getInjuredGamesRemaining() < 5 || !p.isInjured());
    }

    @Test
    void startNextSeason_keepsOverallAndAgesPlayersByOne() {
        controller.startSeason("football");
        Team managed = controller.getSeason().getLeague().getTeams().get(0);
        controller.setUserTeam(managed);
        Player player = managed.getPlayers().get(0);
        int ageBefore = player.getAge();
        int overallBefore = player.getOverall();

        controller.startNextSeason();

        assertEquals(2, controller.getSeason().getSeasonNumber());
        assertEquals(0, controller.getSeason().getCurrentWeek());
        assertEquals(overallBefore, player.getOverall());
        assertEquals(ageBefore + 1, player.getAge());
        assertSame(managed, controller.getSeason().getUserTeam());
    }

    @Test
    void nextWeek_grantsOneTrainingSession() {
        controller.startSeason("football");
        assertEquals(0, controller.getAvailableTrainingSessions());

        controller.nextWeek();

        assertEquals(1, controller.getAvailableTrainingSessions());

        controller.nextWeek();

        assertEquals(1, controller.getAvailableTrainingSessions());
    }

    @Test
    void useTrainingSession_onlyWorksForManagedTeamAndConsumesOne() {
        controller.startSeason("football");
        Team managed = controller.getSeason().getLeague().getTeams().get(0);
        controller.setUserTeam(managed);
        controller.nextWeek();

        Player player = managed.getPlayers().get(0);
        int before = player.getAttribute(managed.getCoach().getSpecialty());

        assertTrue(controller.useTrainingSession(managed));
        assertEquals(0, controller.getAvailableTrainingSessions());
        assertTrue(player.getAttribute(managed.getCoach().getSpecialty()) >= before);
    }
}
