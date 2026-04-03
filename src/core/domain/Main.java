package core.app;

import core.domain.GameWeek;
import core.domain.Match;
import core.domain.StandingEntry;

import java.util.List;

/**
 * Entry point for  mvn exec:java
 * Runs a minimal 3-week football simulation and prints standings.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Sport Management System — M2 Demo ===");

        SeasonController controller = new SeasonController();
        controller.startSeason("football");

        System.out.println("League: " + controller.getSeason().getLeague().getName());
        System.out.println("Teams:  " + controller.getSeason().getLeague().getTeams().size());
        System.out.println("Weeks:  " + controller.getSeason().getGameWeeks().size());
        System.out.println();

        // Simulate 3 weeks
        for (int i = 0; i < 3; i++) {
            GameWeek gw = controller.getSeason().getCurrentGameWeek();
            System.out.println("--- Week " + (gw.getWeekNumber() + 1) + " ---");
            for (Match m : gw.getFixtures()) {
                System.out.printf("  %-20s vs %-20s%n",
                    m.getHomeTeam().getName(), m.getAwayTeam().getName());
            }
            controller.nextWeek();
        }

        System.out.println();
        System.out.println("=== STANDINGS AFTER 3 WEEKS ===");
        List<StandingEntry> table = controller.getStandingsService().getTable();
        int rank = 1;
        for (StandingEntry e : table) {
            System.out.printf("%2d. %-20s  Pts:%2d  W:%2d D:%2d L:%2d  GD:%+3d%n",
                rank++, e.getTeam().getName(), e.getPoints(),
                e.getWon(), e.getDrawn(), e.getLost(), e.getGoalDifference());
        }

        System.out.println("\nDemo complete.");
    }
}
