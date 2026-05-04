package core.app;

import core.domain.GameWeek;
import core.domain.Match;
import core.domain.Season;
import core.domain.StandingEntry;
import core.domain.Player;
import core.domain.Team;
import core.services.SaveLoadService;

import java.util.List;

/**
 * Entry point for: mvn exec:java
 *
 * Demonstrates:
 *   1. Football season (3 weeks) + standings
 *   2. Volleyball season (3 weeks) + standings
 *   3. Save / Load round trip using SaveLoadService
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Sport Management System — M3 Demo ===");
        System.out.println();

        runFootballDemo();
        System.out.println();
        runVolleyballDemo();
        System.out.println();
        runSaveLoadDemo();

        System.out.println();
        System.out.println("Demo complete.");
    }

    private static void runFootballDemo() {
        System.out.println("### FOOTBALL ###");
        SeasonController controller = new SeasonController();
        controller.startSeason("football");

        System.out.println("League : " + controller.getSeason().getLeague().getName());
        System.out.println("Teams  : " + controller.getSeason().getLeague().getTeams().size());
        System.out.println("Weeks  : " + controller.getSeason().getGameWeeks().size());
        System.out.println();

        for (int i = 0; i < 3; i++) {
            GameWeek gw = controller.getSeason().getCurrentGameWeek();
            System.out.println("--- Week " + (gw.getWeekNumber() + 1) + " ---");
            controller.nextWeek();
            for (Match m : gw.getFixtures()) {
                if (m.isPlayed()) {
                    System.out.printf("  %-20s %d - %d  %-20s%n",
                        m.getHomeTeam().getName(),
                        m.getResult().getHomeScore(),
                        m.getResult().getAwayScore(),
                        m.getAwayTeam().getName());
                }
            }
        }

        System.out.println();
        System.out.println("=== FOOTBALL STANDINGS AFTER 3 WEEKS ===");
        printStandings(controller.getStandingsService().getTable(), "GF", "GA", "GD");

        Team sampleTeam = controller.getSeason().getLeague().getTeams().get(0);
        System.out.println();
        System.out.println("=== SAMPLE ROSTER: " + sampleTeam.getName() + " ===");
        if (sampleTeam.getCoach() != null) {
            System.out.printf("Coach: %s (specialty: %s)%n",
                sampleTeam.getCoach().getName(), sampleTeam.getCoach().getSpecialty());
        }
        for (Player p : sampleTeam.getPlayers()) {
            String status = p.isInjured()
                    ? " [INJURED " + p.getInjuredGamesRemaining() + " games]" : "";
            System.out.printf("  #%-3d %-20s %-12s fatigue:%3d%s%n",
                p.getShirtNumber(), p.getName(), p.getPosition(),
                p.getFatigueLevel(), status);
        }
    }

    private static void runVolleyballDemo() {
        System.out.println("### VOLLEYBALL ###");
        SeasonController controller = new SeasonController();
        controller.startSeason("volleyball");

        System.out.println("League : " + controller.getSeason().getLeague().getName());
        System.out.println("Teams  : " + controller.getSeason().getLeague().getTeams().size());
        System.out.println("Weeks  : " + controller.getSeason().getGameWeeks().size());
        System.out.println();

        for (int i = 0; i < 3; i++) {
            GameWeek gw = controller.getSeason().getCurrentGameWeek();
            System.out.println("--- Week " + (gw.getWeekNumber() + 1) + " ---");
            controller.nextWeek();
            for (Match m : gw.getFixtures()) {
                if (m.isPlayed()) {
                    System.out.printf("  %-20s %d - %d  %-20s (sets)%n",
                        m.getHomeTeam().getName(),
                        m.getResult().getHomeScore(),
                        m.getResult().getAwayScore(),
                        m.getAwayTeam().getName());
                }
            }
        }

        System.out.println();
        System.out.println("=== VOLLEYBALL STANDINGS AFTER 3 WEEKS ===");
        printStandings(controller.getStandingsService().getTable(), "SF", "SA", "SD");
    }

    private static void runSaveLoadDemo() {
        System.out.println("### SAVE / LOAD ###");
        SeasonController controller = new SeasonController();
        controller.startSeason("football");
        controller.nextWeek();
        controller.nextWeek();

        SaveLoadService svc = new SaveLoadService();
        int slot = 1;

        try {
            svc.save(controller.getSeason(), slot);
            System.out.println("Saved season to slot " + slot +
                    " (week " + controller.getSeason().getCurrentWeek() + ").");

            Season loaded = svc.load(slot);
            System.out.println("Loaded season back: league='" + loaded.getLeague().getName() +
                    "', currentWeek=" + loaded.getCurrentWeek() +
                    ", teams=" + loaded.getLeague().getTeams().size() +
                    ", weeks=" + loaded.getGameWeeks().size());
            System.out.println("Round-trip successful.");
        } catch (Exception e) {
            System.err.println("Save/Load failed: " + e.getMessage());
        }
    }

    private static void printStandings(List<StandingEntry> table,
                                       String forCol, String againstCol, String diffCol) {
        System.out.printf("%-4s %-20s %4s  %3s %3s %3s  %4s %4s  %4s%n",
                "#", "TEAM", "Pts", "W", "D", "L", forCol, againstCol, diffCol);
        System.out.println("-".repeat(60));
        int rank = 1;
        for (StandingEntry e : table) {
            System.out.printf("%-4d %-20s %4d  %3d %3d %3d  %4d %4d  %+4d%n",
                rank++, e.getTeam().getName(), e.getPoints(),
                e.getWon(), e.getDrawn(), e.getLost(),
                e.getGoalsFor(), e.getGoalsAgainst(), e.getGoalDifference());
        }
    }
}
