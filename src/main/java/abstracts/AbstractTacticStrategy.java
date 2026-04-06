package abstracts;

import core.domain.Match;
import core.domain.Team;
import interfaces.ITacticStrategy;
import valueobjects.TacticResult;

public abstract class AbstractTacticStrategy implements ITacticStrategy {

    @Override
    public abstract TacticResult applyTactic(Team team, Match match);

    @Override
    public float getFatigueMultiplier() { return 1.0f; } // subclasses override as needed
}
