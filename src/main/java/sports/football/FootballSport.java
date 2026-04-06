package sports.football;

import abstracts.AbstractSport;
import core.domain.Player;
import core.domain.Team;
import core.services.PlayerGenerator;
import interfaces.IMatchSimulator;
import interfaces.IStandingsCalculator;
import valueobjects.RosterRules;
import valueobjects.ScoringConfig;

import java.util.List;

public class FootballSport extends AbstractSport {

    public FootballSport() {
        super(new RosterRules(18, 7, 3), ScoringConfig.footballDefaults());
    }

    @Override public String getName()         { return "Football"; }
    @Override public int    getMatchPeriods() { return 2; }

    @Override public List<Player> generatePlayers(Team team) {
        return PlayerGenerator.generateForSport(this, team);
    }

    @Override public IMatchSimulator      createMatchSimulator()      { return new FootballMatchSimulator(); }
    @Override public IStandingsCalculator createStandingsCalculator() { return new FootballStandingsCalculator(); }
}
