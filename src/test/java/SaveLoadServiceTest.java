import core.app.SeasonController;
import core.domain.Season;
import core.services.SaveLoadService;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SaveLoadServiceTest {

    @Test
    void saveAndLoadFootballSeasonRoundTrip() throws Exception {
        SeasonController controller = new SeasonController();
        controller.startSeason("football");
        controller.nextWeek();

        SaveLoadService svc = new SaveLoadService();
        int slot = 99;
        svc.save(controller.getSeason(), slot);
        assertTrue(svc.slotExists(slot));

        Season loaded = svc.load(slot);
        assertNotNull(loaded);
        assertEquals(controller.getSeason().getCurrentWeek(), loaded.getCurrentWeek());
        assertEquals(controller.getSeason().getLeague().getName(), loaded.getLeague().getName());
        assertEquals(controller.getSeason().getLeague().getTeams().size(),
                     loaded.getLeague().getTeams().size());

        new File("saves/slot_" + slot + ".ser").delete();
    }

    @Test
    void saveAndLoadVolleyballSeasonRoundTrip() throws Exception {
        SeasonController controller = new SeasonController();
        controller.startSeason("volleyball");
        controller.nextWeek();

        SaveLoadService svc = new SaveLoadService();
        int slot = 98;
        svc.save(controller.getSeason(), slot);

        Season loaded = svc.load(slot);
        assertEquals("Volleyball League", loaded.getLeague().getName());
        assertEquals(controller.getSeason().getGameWeeks().size(),
                     loaded.getGameWeeks().size());

        new File("saves/slot_" + slot + ".ser").delete();
    }

    @Test
    void slotExistsReturnsFalseForUnusedSlot() {
        SaveLoadService svc = new SaveLoadService();
        assertFalse(svc.slotExists(42));
    }

    @Test
    void resolveSportReturnsCorrectType() {
        SaveLoadService svc = new SaveLoadService();
        assertEquals("Football",  svc.resolveSport("football").getName());
        assertEquals("Volleyball", svc.resolveSport("volleyball").getName());
    }

    @Test
    void resolveSportThrowsOnUnknown() {
        SaveLoadService svc = new SaveLoadService();
        assertThrows(IllegalArgumentException.class, () -> svc.resolveSport("basketball"));
    }
}
