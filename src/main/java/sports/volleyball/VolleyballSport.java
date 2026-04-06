package sports.volleyball;

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

public class VolleyballSport extends AbstractSport {

    private static final String[] POSITIONS = {
        "Setter","Libero","MiddleBlocker","OutsideHitter","OutsideHitter","MiddleBlocker"
    };
    private static final String[] ATTRS = {
        "serving","blocking","spiking","receiving","jump","stamina","reach"
    };

    public VolleyballSport() {
        super(new RosterRules(12, 6, 6), new ScoringConfig(3, 0, 0));
    }

    @Override public String getName()         { return "Volleyball"; }
    @Override public int    getMatchPeriods() { return 5; }

    @Override
    public List<Player> generatePlayers(Team team) {
        Random rng = new Random();
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < rosterRules.getRosterSize(); i++) {
            Player p = new Player("Player " + (i+1), POSITIONS[i % POSITIONS.length],
                                  18 + rng.nextInt(17), i + 1);
            for (String a : ATTRS) p.setAttribute(a, rng.nextInt(100) + 1);
            list.add(p);
        }
        return list;
    }

    @Override public IMatchSimulator      createMatchSimulator()      {
        throw new UnsupportedOperationException("VolleyballMatchSimulator — M3 deliverable");
    }
    @Override public IStandingsCalculator createStandingsCalculator() {
        throw new UnsupportedOperationException("VolleyballStandingsCalculator — M3 deliverable");
    }
}
