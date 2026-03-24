package tactics;

import abstracts.AbstractTacticStrategy;
import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

public class BalancedStrategy extends AbstractTacticStrategy {
    @Override public TacticResult applyTactic(Team team, Match match) {
        return new TacticResult(0.6f, 0.5f, getFormation());
    }
    @Override public String getName()      { return "Balanced"; }
    @Override public String getFormation() { return "4-4-2"; }
    @Override public float  getFatigueMultiplier() { return 1.0f; }
}
