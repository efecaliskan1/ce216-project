package interfaces;

import core.domain.Match;
import core.domain.TacticResult;
import core.domain.Team;

// contract for tactical strategies
public interface ITacticStrategy {

    // applies tactic and returns tactical values for simulator [cite: 25]
    TacticResult applyTactic(Team team, Match match);

    // returns tactic name like high press or defensive [cite: 26, 287, 290]
    String getName();

    // returns formation string for ui [cite: 27, 44, 291]
    String getFormation();

    // returns multiplier for post match fatigue [cite: 28, 61, 260]
    float getFatigueMultiplier();
}