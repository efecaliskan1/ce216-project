package tactics;

import abstracts.AbstractTacticStrategy;
import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

public class DefensiveStrategy extends AbstractTacticStrategy {
    @Override public TacticResult applyTactic(Team team, Match match) {
        return new TacticResult(0.4f, 0.3f, getFormation());
    }
    @Override public String getName()      { return "Defensive"; }
    @Override public String getFormation() { return "5-4-1"; }
    @Override public float  getFatigueMultiplier() { return 0.8f; }
}
