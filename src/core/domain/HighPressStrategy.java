package tactics;

import abstracts.AbstractTacticStrategy;
import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

public class HighPressStrategy extends AbstractTacticStrategy {
    @Override public TacticResult applyTactic(Team team, Match match) {
        return new TacticResult(0.9f, 0.8f, getFormation());
    }
    @Override public String getName()      { return "HighPress"; }
    @Override public String getFormation() { return "4-3-3"; }
    @Override public float  getFatigueMultiplier() { return 1.4f; }
}
