package abstracts;

import core.domain.Player;
import core.domain.Team;
import interfaces.ISport;
import interfaces.IMatchSimulator;
import interfaces.IStandingsCalculator;
import valueobjects.RosterRules;
import valueobjects.ScoringConfig;

import java.util.List;

public abstract class AbstractSport implements ISport {

    protected RosterRules  rosterRules;
    protected ScoringConfig scoringConfig;

    protected AbstractSport(RosterRules rosterRules, ScoringConfig scoringConfig) {
        this.rosterRules   = rosterRules;
        this.scoringConfig = scoringConfig;
    }

    /** Shared lineup validation: correct size and all players available. */
    public boolean validateLineup(List<Player> lineup) {
        if (lineup.size() != rosterRules.getStartingLineupSize()) return false;
        for (Player p : lineup) if (!p.isAvailable()) return false;
        return true;
    }

    @Override public RosterRules getRosterRules() { return rosterRules; }
    public ScoringConfig getScoringConfig()        { return scoringConfig; }

    @Override public abstract List<Player> generatePlayers(Team team);
    @Override public abstract IMatchSimulator createMatchSimulator();
    @Override public abstract IStandingsCalculator createStandingsCalculator();
}
