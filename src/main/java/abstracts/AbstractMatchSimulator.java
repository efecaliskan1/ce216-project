package abstracts;

import core.domain.*;
import interfaces.IMatchSimulator;
import interfaces.MatchObserver;
import valueobjects.PeriodResult;
import valueobjects.TacticResult;

import java.util.Random;

public abstract class AbstractMatchSimulator implements IMatchSimulator {

    protected Random        random   = new Random();
    protected MatchObserver observer;

    @Override public void setSeed(long seed)            { random = new Random(seed); }
    @Override public void setObserver(MatchObserver obs){ this.observer = obs; }

    public abstract PeriodResult simulatePeriod(
            Team home, Team away,
            TacticResult homeApplied, TacticResult awayApplied,
            int period);

    /** Fires the right observer method; null-safe. */
    public void fireEvent(MatchEvent event) {
        if (observer == null) return;
        switch (event.getType()) {
            case GOAL         -> observer.onGoal(event);
            case INJURY       -> observer.onInjury(event);
            case SUBSTITUTION -> observer.onSubstitution(event);
            default           -> {}
        }
    }

    /** injury chance = 2% base + up to 8% from fatigue */
    public boolean rollInjury(Player p) {
        double chance = 0.02 + (p.getFatigueLevel() / 100.0) * 0.08;
        return random.nextDouble() < chance;
    }
}
