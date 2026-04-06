package core.app;

import core.domain.Match;
import core.domain.MatchResult;
import interfaces.IMatchSimulator;
import interfaces.ISport;
import interfaces.MatchObserver;

public class MatchCoordinator {

    private final ISport sport;
    private MatchObserver observer;

    public MatchCoordinator(ISport sport) { this.sport = sport; }

    public void setObserver(MatchObserver observer) { this.observer = observer; }

    public MatchResult executeMatch(Match match) {
        IMatchSimulator sim = sport.createMatchSimulator();
        if (observer != null) sim.setObserver(observer);
        return sim.simulate(match);
    }

    public MatchResult executeMatchWithSeed(Match match, long seed) {
        IMatchSimulator sim = sport.createMatchSimulator();
        sim.setSeed(seed);
        if (observer != null) sim.setObserver(observer);
        return sim.simulate(match);
    }
}
