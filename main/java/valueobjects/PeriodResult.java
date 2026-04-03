package valueobjects;

import core.domain.MatchEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PeriodResult implements Serializable {
    private final int homeScore;
    private final int awayScore;
    private final List<MatchEvent> events;

    public PeriodResult(int homeScore, int awayScore, List<MatchEvent> events) {
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.events    = Collections.unmodifiableList(new ArrayList<>(events));
    }

    public int getHomeScore()          { return homeScore; }
    public int getAwayScore()          { return awayScore; }
    public List<MatchEvent> getEvents(){ return events; }
}
