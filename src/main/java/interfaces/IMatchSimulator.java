package interfaces;

import core.domain.Match;
import core.domain.MatchResult;

public interface IMatchSimulator {
    MatchResult simulate(Match match);
    void setObserver(MatchObserver observer);
    void setSeed(long seed);
}
