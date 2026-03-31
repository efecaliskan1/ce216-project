package interfaces;

import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

public interface ITacticStrategy {
    TacticResult applyTactic(Team team, Match match);
    String getName();
    String getFormation();
    float getFatigueMultiplier();
}
