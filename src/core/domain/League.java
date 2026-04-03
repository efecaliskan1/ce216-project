package core.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class League implements Serializable {
    private final String name;
    private final List<Team> teams;
    private final List<Match> fixtures;
    private final List<MatchResult> matchHistory;

    public League(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }

        this.name = name;
        this.teams = new ArrayList<>();
        this.fixtures = new ArrayList<>();
        this.matchHistory = new ArrayList<>();
    }

    public void addTeam(Team team) {
        if (team != null) {
            teams.add(team);
        }
    }

    public void addFixture(Match match) {
        if (match != null) {
            fixtures.add(match);
        }
    }

    public void addResult(MatchResult result) {
        if (result != null) {
            matchHistory.add(result);
        }
    }

    public List<MatchResult> getMatchHistory() {
        return Collections.unmodifiableList(matchHistory);
    }

    public List<Match> getFixturesForWeek(int weekNumber) {
        return fixtures.stream()
                .filter(match -> match.getWeekNumber() == weekNumber)
                .collect(Collectors.toList());
    }

    public List<Match> getFixturesForTeam(Team team) {
        if (team == null) {
            return Collections.emptyList();
        }

        return fixtures.stream()
                .filter(match -> match.getHomeTeam().equals(team) || match.getAwayTeam().equals(team))
                .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public List<Team> getTeams() {
        return Collections.unmodifiableList(teams);
    }

    public List<Match> getFixtures() {
        return Collections.unmodifiableList(fixtures);
    }
}
