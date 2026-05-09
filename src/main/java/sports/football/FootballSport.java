package sports.football;

import abstracts.AbstractSport;
import core.domain.Player;
import core.domain.Team;
import interfaces.IMatchSimulator;
import interfaces.IStandingsCalculator;
import valueobjects.RosterRules;
import valueobjects.ScoringConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FootballSport extends AbstractSport {

    private static final String[] POSITIONS = {
        "Goalkeeper","Goalkeeper","Goalkeeper",
        "Defender","Defender","Defender","Defender","Defender","Defender","Defender","Defender",
        "Midfielder","Midfielder","Midfielder","Midfielder","Midfielder","Midfielder","Midfielder","Midfielder",
        "Striker","Striker","Striker","Striker","Striker","Striker"
    };
    private static final String[] ATTRS = {
        "finishing","passing","tackling","dribbling","pace","stamina","strength"
    };

    public FootballSport() {
        super(new RosterRules(25, 14, 3), ScoringConfig.footballDefaults());
    }

    @Override public String getName()         { return "Football"; }
    @Override public int    getMatchPeriods() { return 2; }

    @Override
    public List<Player> generatePlayers(Team team) {
        Random rng = new Random();
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < rosterRules.getRosterSize(); i++) {
            Player p = new Player("Player " + (i + 1),
                                  POSITIONS[i % POSITIONS.length],
                                  17 + rng.nextInt(19),
                                  i + 1);
            for (String a : ATTRS) p.setAttribute(a, rng.nextInt(100) + 1);
            list.add(p);
        }
        return list;
    }

    @Override public IMatchSimulator      createMatchSimulator()      { return new FootballMatchSimulator(); }
    @Override public IStandingsCalculator createStandingsCalculator() { return new FootballStandingsCalculator(); }
}
