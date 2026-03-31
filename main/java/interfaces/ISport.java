package interfaces;

import core.domain.Player;
import core.domain.Team;
import valueobjects.RosterRules;

import java.util.List;

public interface ISport {
    String getName();
    IMatchSimulator createMatchSimulator();
    IStandingsCalculator createStandingsCalculator();
    List<Player> generatePlayers(Team team);
    
    RosterRules getRosterRules();
    int getMatchPeriods();
}
