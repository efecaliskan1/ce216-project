package interfaces;

import core.domain.Match;
import core.domain.MatchResult;

// match simulation engine contract
public interface IMatchSimulator {

    // runs match simulation and returns immutable result
    MatchResult simulate(Match match);

    // sets observer for match events like goals and injuries
    void setObserver(MatchObserver observer);

    // seeds rng for deterministic results and testing
    void setSeed(long seed);
}