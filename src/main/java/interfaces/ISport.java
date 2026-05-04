package interfaces;

import core.domain.Player;
import core.domain.Team;
import valueobjects.RosterRules;

import java.io.Serializable;
import java.util.List;

public interface ISport extends Serializable {
    String getName();
    IMatchSimulator createMatchSimulator();
    IStandingsCalculator createStandingsCalculator();
    List<Player> generatePlayers(Team team);
    RosterRules getRosterRules();
    int getMatchPeriods();
}
