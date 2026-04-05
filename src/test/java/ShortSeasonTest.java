import core.app.SeasonController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ShortSeasonTest {

    private SeasonController controller;

    @BeforeEach
    void setUp() {
        controller = new SeasonController();
        controller.startSeason("football");
    }

    @Test
    void thirtyWeeks_seasonIsFinished() {
        for (int i = 0; i < 30; i++) controller.nextWeek();
        assertTrue(controller.getSeason().isFinished());
    }

    @Test
    void afterFullSeason_winnerIsPresent() {
        for (int i = 0; i < 30; i++) controller.nextWeek();
        assertTrue(controller.getSeason().getWinner().isPresent());
    }

    @Test
    void afterFullSeason_allMatchesPlayed() {
        for (int i = 0; i < 30; i++) controller.nextWeek();
        long unplayed = controller.getSeason().getLeague().getFixtures()
                .stream().filter(m -> !m.isPlayed()).count();
        assertEquals(0, unplayed);
    }

    @Test
    void afterFullSeason_standingsHave16Teams() {
        for (int i = 0; i < 30; i++) controller.nextWeek();
        assertEquals(16, controller.getStandingsService().getTable().size());
    }
}
