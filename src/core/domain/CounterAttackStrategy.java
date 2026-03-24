package tactics;

import abstracts.AbstractTacticStrategy;
import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

public class CounterAttackStrategy extends AbstractTacticStrategy {
    @Override public TacticResult applyTactic(Team team, Match match) {
        return new TacticResult(0.5f, 0.7f, getFormation());
    }
    @Override public String getName()      { return "CounterAttack"; }
    @Override public String getFormation() { return "4-5-1"; }
    @Override public float  getFatigueMultiplier() { return 0.9f; }
}
