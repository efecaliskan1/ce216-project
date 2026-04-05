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
}
