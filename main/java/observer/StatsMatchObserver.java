package observer;

import core.domain.MatchEvent;
import core.domain.MatchResult;
import core.domain.Player;
import core.domain.Team;
import interfaces.MatchObserver;

import java.util.*;


public class StatsMatchObserver implements MatchObserver {

    private final List<MatchEvent> goalLog    = new ArrayList<>();
    private final List<MatchEvent> injuryLog  = new ArrayList<>();
    private final List<MatchEvent> allEvents  = new ArrayList<>();


    private final Map<Player, Integer> performanceScores = new HashMap<>();

  

    @Override
    public void onGoal(MatchEvent event) {
        goalLog.add(event);
        allEvents.add(event);
        performanceScores.merge(event.getPlayer(), 1, Integer::sum);
    }

    @Override
    public void onInjury(MatchEvent event) {
        injuryLog.add(event);
        allEvents.add(event);
    }

    @Override
    public void onSubstitution(MatchEvent event) {
        allEvents.add(event);
    }

    @Override
    public void onPeriodEnd(int period, int homeScore, int awayScore) {
      
    }

    @Override
    public void onMatchEnd(MatchResult result) {
      
    }

   
   
    public int getGoalCount(Team team) {
        return (int) goalLog.stream()
                .filter(e -> e.getTeam().equals(team))
                .count();
    }

   
    public Map<Player, Integer> getPerformanceScores() {
        return Collections.unmodifiableMap(performanceScores);
    }

  
    public List<MatchEvent> getGoalLog() {
        return Collections.unmodifiableList(goalLog);
    }

   
    public List<MatchEvent> getInjuryLog() {
        return Collections.unmodifiableList(injuryLog);
    }

  
    public List<MatchEvent> getAllEvents() {
        return Collections.unmodifiableList(allEvents);
    }

  
    public void reset() {
        goalLog.clear();
        injuryLog.clear();
        allEvents.clear();
        performanceScores.clear();
    }
}