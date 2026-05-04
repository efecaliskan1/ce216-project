package interfaces;

import core.domain.Match;
import core.domain.Team;
import valueobjects.TacticResult;

import java.io.Serializable;

public interface ITacticStrategy extends Serializable {
    TacticResult applyTactic(Team team, Match match);
    String getName();
    String getFormation();
    float getFatigueMultiplier();
}
