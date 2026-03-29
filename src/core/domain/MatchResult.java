package core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class MatchResult {
    private final int homeScore;
    private final int awayScore;
    private final Team homeTeam;
    private final Team awayTeam;
    private final List<MatchEvent> events;
    private final int weekNumber;

    public MatchResult(
            int homeScore,
            int awayScore,
            Team homeTeam,
            Team awayTeam,
            List<MatchEvent> events,
            int weekNumber
    ) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.events = Collections.unmodifiableList(new ArrayList<>(events == null ? Collections.<MatchEvent>emptyList() : events));
        this.weekNumber = weekNumber;
    }

    public Optional<Team> getWinner() {
        if (homeScore > awayScore) {
            return Optional.of(homeTeam);
        }
        if (awayScore > homeScore) {
            return Optional.of(awayTeam);
        }
        return Optional.empty();
    }

    public boolean isDraw() {
        return homeScore == awayScore;
    }

    public int getGoalsFor(Team team) {
        if (team == null) {
            return 0;
        }
        if (team.equals(homeTeam)) {
            return homeScore;
        }
        if (team.equals(awayTeam)) {
            return awayScore;
        }
        return 0;
    }

    public int getGoalsAgainst(Team team) {
        if (team == null) {
            return 0;
        }
        if (team.equals(homeTeam)) {
            return awayScore;
        }
        if (team.equals(awayTeam)) {
            return homeScore;
        }
        return 0;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public List<MatchEvent> getEvents() {
        return events;
    }

    public int getWeekNumber() {
        return weekNumber;
    }
}
