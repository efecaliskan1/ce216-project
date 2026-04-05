import core.domain.League;
import core.domain.Match;
import core.domain.Team;
import core.services.FixtureGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FixtureGeneratorTest {

    private League league;

    @BeforeEach
    void setUp() {
        league = new League("Test League");
        for (int i = 1; i <= 16; i++)
            league.addTeam(new Team("Team" + i, "logo.png", RosterRules.defaults()));
    }

    @Test
    void sixteenTeams_generates240Matches() {
        new FixtureGenerator().generate(league);
        assertEquals(240, league.getFixtures().size());
    }

    @Test
    void eachTeamPlays30Matches() {
        new FixtureGenerator().generate(league);
        for (Team t : league.getTeams()) {
            long count = league.getFixtures().stream()
                .filter(m -> m.getHomeTeam().equals(t) || m.getAwayTeam().equals(t))
                .count();
            assertEquals(30, count, t.getName() + " should play 30 matches");
        }
    }

    @Test
    void noTeamAppearsInSameWeekTwice() {
        new FixtureGenerator().generate(league);
        int maxWeek = league.getFixtures().stream().mapToInt(Match::getWeekNumber).max().orElse(0);
        for (int w = 0; w <= maxWeek; w++) {
            final int week = w;
            Set<Team> seen = new HashSet<>();
            for (Match m : league.getFixtures()) {
                if (m.getWeekNumber() != week) continue;
                assertTrue(seen.add(m.getHomeTeam()));
                assertTrue(seen.add(m.getAwayTeam()));
            }
        }
    }

    @Test
    void noDuplicateFixturesInSameWeek() {
        new FixtureGenerator().generate(league);
        Set<String> keys = new HashSet<>();
        for (Match m : league.getFixtures()) {
            String key = m.getHomeTeam().getName() + "_" + m.getAwayTeam().getName() + "_w" + m.getWeekNumber();
            assertTrue(keys.add(key), "Duplicate fixture: " + key);
        }
    }
}
