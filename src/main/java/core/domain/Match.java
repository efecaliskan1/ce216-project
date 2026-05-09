package core.domain;

import valueobjects.TacticResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Match implements Serializable {

    private Team homeTeam;
    private Team awayTeam;
    private TacticResult homeAppliedTactic;
    private TacticResult awayAppliedTactic;
    private List<MatchEvent> eventLog;
    private List<Substitution> substitutions;
    private MatchResult result;
    private boolean isPlayed;
    private int weekNumber;
    private int currentPeriod;

    public Match(Team homeTeam, Team awayTeam, int weekNumber) {
        this.homeTeam      = homeTeam;
        this.awayTeam      = awayTeam;
        this.weekNumber    = weekNumber;
        this.eventLog      = new ArrayList<>();
        this.substitutions = new ArrayList<>();
    }

    public void applyCurrentTactics() {
        homeAppliedTactic = homeTeam.getCurrentTactic().applyTactic(homeTeam, this);
        awayAppliedTactic = awayTeam.getCurrentTactic().applyTactic(awayTeam, this);
    }

    public void addSubstitution(Substitution s) { substitutions.add(s); }

    public Team               getHomeTeam()         { return homeTeam; }
    public Team               getAwayTeam()         { return awayTeam; }
    public MatchResult        getResult()           { return result; }
    public void               setResult(MatchResult r){ this.result = r; }
    public boolean            isPlayed()            { return isPlayed; }
    public void               setPlayed(boolean b)  { isPlayed = b; }
    public int                getWeekNumber()       { return weekNumber; }
    public List<Substitution> getSubstitutions()    { return substitutions; }
    public List<MatchEvent>   getEventLog()         { return eventLog; }

    public TacticResult       getHomeAppliedTactic(){ return homeAppliedTactic; }
    public TacticResult       getAwayAppliedTactic(){ return awayAppliedTactic; }
    public int                getCurrentPeriod()    { return currentPeriod; }
    public void               setCurrentPeriod(int p){ this.currentPeriod = p; }
}
