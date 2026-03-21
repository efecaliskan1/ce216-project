package interfaces;

import core.domain.MatchEvent;
import core.domain.MatchResult;

// observer contract for in-match events
public interface MatchObserver {

    // called when a goal is scored [cite: 30]
    void onGoal(MatchEvent event);

    // called when a player is injured during the match [cite: 31]
    void onInjury(MatchEvent event);

    // called when a substitution is made [cite: 32]
    void onSubstitution(MatchEvent event);

    // called at the end of each period [cite: 33]
    void onPeriodEnd(int period, int homeScore, int awayScore);

    // called once when the entire match is finished [cite: 34]
    void onMatchEnd(MatchResult result);
}