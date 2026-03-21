package core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// immutable outcome of a single match period
public final class PeriodResult {

    private final int homeScore;
    private final int awayScore;
    private final List<MatchEvent> events;

    // stores scores and events for the period
    public PeriodResult(int homeScore, int awayScore, List<MatchEvent> events) {
        if (homeScore < 0 || awayScore < 0) {
            throw new IllegalArgumentException("Scores must be non-negative");
        }
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.events = Collections.unmodifiableList(
                new ArrayList<>(events == null ? Collections.emptyList() : events));
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    // unmodifiable list of period events
    public List<MatchEvent> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return "PeriodResult{home=" + homeScore + ", away=" + awayScore
                + ", events=" + events.size() + '}';
    }
}