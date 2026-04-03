package interfaces;

import core.domain.MatchEvent;
import core.domain.MatchResult;

public interface MatchObserver {
    void onGoal(MatchEvent event);
    void onInjury(MatchEvent event);
    void onSubstitution(MatchEvent event);
    void onPeriodEnd(int period, int homeScore, int awayScore);
    void onMatchEnd(MatchResult result);
}
