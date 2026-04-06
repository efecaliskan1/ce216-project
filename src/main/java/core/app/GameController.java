package core.app;

import core.domain.Match;
import core.domain.StandingEntry;
import interfaces.IUIController;

import java.util.List;

public class GameController implements IUIController {

    private final SeasonController seasonController;

    public GameController(SeasonController sc) { this.seasonController = sc; }

    @Override
    public void startSeason(String sportName) {
        seasonController.startSeason(sportName);
        System.out.println("Season started: " + sportName);
    }

    @Override
    public void nextWeek() {
        seasonController.nextWeek();
        if (seasonController.getSeason() != null) {
            System.out.println("Week " + seasonController.getSeason().getCurrentWeek());
            if (seasonController.getSeason().isFinished())
                seasonController.getSeason().getWinner()
                    .ifPresent(w -> System.out.println("Winner: " + w.getName()));
        }
    }

    @Override
    public void showStandings() {
        if (seasonController.getStandingsService() == null) return;
        List<StandingEntry> table = seasonController.getStandingsService().getTable();
        System.out.println("=== STANDINGS ===");
        int rank = 1;
        for (StandingEntry e : table) {
            System.out.printf("%2d. %-20s Pts:%2d W:%2d D:%2d L:%2d GD:%+3d%n",
                rank++, e.getTeam().getName(), e.getPoints(),
                e.getWon(), e.getDrawn(), e.getLost(), e.getGoalDifference());
        }
    }

    @Override
    public void showMatch(Match match) {
        System.out.printf("%s vs %s", match.getHomeTeam().getName(), match.getAwayTeam().getName());
        if (match.isPlayed())
            System.out.printf("  →  %d - %d%n",
                match.getResult().getHomeScore(), match.getResult().getAwayScore());
        else System.out.println("  (not played)");
    }

    public SeasonController getSeasonController() { return seasonController; }
}
