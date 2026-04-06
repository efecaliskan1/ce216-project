package core.app;

import core.domain.GameWeek;
import core.domain.Match;
import core.domain.StandingEntry;
import core.domain.Player;
import core.domain.Team;

import java.util.List;

/**
 * Entry point for  mvn exec:java
 * Runs a minimal 3-week football simulation and prints standings.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Sport Management System — M2 Demo ===");
        System.out.println();

        SeasonController controller = new SeasonController();
        controller.startSeason("football");

        System.out.println("League : " + controller.getSeason().getLeague().getName());
        System.out.println("Teams  : " + controller.getSeason().getLeague().getTeams().size());
        System.out.println("Weeks  : " + controller.getSeason().getGameWeeks().size());
        System.out.println();

        // Simulate 3 weeks
        for (int i = 0; i < 3; i++) {
            GameWeek gw = controller.getSeason().getCurrentGameWeek();
            System.out.println("--- Week " + (gw.getWeekNumber() + 1) + " ---");
            controller.nextWeek();

            // Print results after playing
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
        System.out.println("=== STANDINGS AFTER 3 WEEKS ===");
        List<StandingEntry> table = controller.getStandingsService().getTable();
        System.out.printf("%-4s %-20s %4s  %3s %3s %3s  %4s %4s  %4s%n",
                "#", "TEAM", "Pts", "W", "D", "L", "GF", "GA", "GD");
        System.out.println("-".repeat(60));
        int rank = 1;
        for (StandingEntry e : table) {
            System.out.printf("%-4d %-20s %4d  %3d %3d %3d  %4d %4d  %+4d%n",
                rank++, e.getTeam().getName(), e.getPoints(),
                e.getWon(), e.getDrawn(), e.getLost(),
                e.getGoalsFor(), e.getGoalsAgainst(), e.getGoalDifference());
        }

        // Print sample team roster
        Team sampleTeam = controller.getSeason().getLeague().getTeams().get(0);
        System.out.println();
        System.out.println("=== SAMPLE ROSTER: " + sampleTeam.getName() + " ===");
        System.out.printf("Coach: %s (specialty: %s)%n",
            sampleTeam.getCoach().getName(), sampleTeam.getCoach().getSpecialty());
        for (Player p : sampleTeam.getPlayers()) {
            String status = p.isInjured() ?
                    " [INJURED " + p.getInjuredGamesRemaining() + " games]" : "";
            System.out.printf("  #%-3d %-20s %-12s fatigue:%3d%s%n",
                p.getShirtNumber(), p.getName(), p.getPosition(),
                p.getFatigueLevel(), status);
        }

        System.out.println();
        System.out.println("Demo complete.");
    }
}
