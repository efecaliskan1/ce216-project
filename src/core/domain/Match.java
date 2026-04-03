package core.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Match {
    private final Team homeTeam;
    private final Team awayTeam;
    private final int weekNumber;
    private final List<MatchEvent> eventLog;
    private final List<Substitution> substitutions;
    private TacticResult homeAppliedTactic;
    private TacticResult awayAppliedTactic;
    private MatchResult result;
    private boolean played;
    private int currentPeriod;

    public Match(Team homeTeam, Team awayTeam) {
        this(homeTeam, awayTeam, 0);
    }

    public Match(Team homeTeam, Team awayTeam, int weekNumber) {
        if (homeTeam == null) {
            throw new IllegalArgumentException("homeTeam cannot be null");
        }
        if (awayTeam == null) {
            throw new IllegalArgumentException("awayTeam cannot be null");
        }
        if (homeTeam.equals(awayTeam)) {
            throw new IllegalArgumentException("homeTeam and awayTeam must be different");
        }
        if (weekNumber < 0) {
            throw new IllegalArgumentException("weekNumber cannot be negative");
        }

        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.weekNumber = weekNumber;
        this.eventLog = new ArrayList<>();
        this.substitutions = new ArrayList<>();
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public TacticResult getHomeAppliedTactic() {
        return homeAppliedTactic;
    }

    public TacticResult getAwayAppliedTactic() {
        return awayAppliedTactic;
    }

    public void applyCurrentTactics() {
        if (homeTeam.getCurrentTactic() != null) {
            homeAppliedTactic = homeTeam.getCurrentTactic().applyTactic(homeTeam, this);
        }
        if (awayTeam.getCurrentTactic() != null) {
            awayAppliedTactic = awayTeam.getCurrentTactic().applyTactic(awayTeam, this);
        }
    }

    public void addEvent(MatchEvent event) {
        if (event != null) {
            if (!event.getTeam().equals(homeTeam) && !event.getTeam().equals(awayTeam)) {
                throw new IllegalArgumentException("event team must belong to this match");
            }
            eventLog.add(event);
        }
    }

    public List<MatchEvent> getEventLog() {
        return Collections.unmodifiableList(eventLog);
    }

    public void addSubstitution(Substitution substitution) {
        if (substitution != null) {
            substitutions.add(substitution);
        }
    }

    public List<Substitution> getSubstitutions() {
        return Collections.unmodifiableList(substitutions);
    }

    public MatchResult getResult() {
        return result;
    }

    public void setResult(MatchResult result) {
        if (result != null) {
            boolean sameTeams = result.getHomeTeam().equals(homeTeam) && result.getAwayTeam().equals(awayTeam);
            if (!sameTeams) {
                throw new IllegalArgumentException("result teams must match this match");
            }
            if (result.getWeekNumber() != weekNumber) {
                throw new IllegalArgumentException("result weekNumber must match this match");
            }
        }
        this.result = result;
        this.played = result != null;
    }

    public boolean isPlayed() {
        return played;
    }

    public void setPlayed(boolean played) {
        this.played = played;
    }

    public int getCurrentPeriod() {
        return currentPeriod;
    }

    public void setCurrentPeriod(int currentPeriod) {
        this.currentPeriod = currentPeriod;
    }
}
