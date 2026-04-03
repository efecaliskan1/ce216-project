package abstracts;

import core.domain.Player;
import core.domain.Team;
import interfaces.IMatchSimulator;
import interfaces.IStandingsCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import valueobjects.RosterRules;
import valueobjects.ScoringConfig;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AbstractSportTest {

    
    private static class TestSport extends AbstractSport {
        TestSport(RosterRules rules) {
            super(rules, new ScoringConfig(3, 1, 0));
        }
        @Override public String getName()                              { return "Test"; }
        @Override public int    getMatchPeriods()                      { return 2; }
        @Override public List<Player> generatePlayers(Team team)       { return List.of(); }
        @Override public IMatchSimulator createMatchSimulator()        { return null; }
        @Override public IStandingsCalculator createStandingsCalculator() { return null; }
    }

    private static final RosterRules RULES = new RosterRules(18, 7, 3); 
    private TestSport sport;

    @BeforeEach
    void setUp() {
        sport = new TestSport(RULES);
    }

    

    @Test
    void validateLineup_11HealthyPlayers_returnsTrue() {
        List<Player> lineup = healthyPlayers(11);
        assertTrue(sport.validateLineup(lineup));
    }

    @Test
    void validateLineup_10Players_returnsFalse() {
        List<Player> lineup = healthyPlayers(10);
        assertFalse(sport.validateLineup(lineup));
    }

    @Test
    void validateLineup_12Players_returnsFalse() {
        List<Player> lineup = healthyPlayers(12);
        assertFalse(sport.validateLineup(lineup));
    }

    @Test
    void validateLineup_11PlayersOneInjured_returnsFalse() {
        List<Player> lineup = healthyPlayers(11);
        lineup.get(0).applyInjury(3); 
        assertFalse(sport.validateLineup(lineup));
    }

    @Test
    void validateLineup_emptyList_returnsFalse() {
        assertFalse(sport.validateLineup(new ArrayList<>()));
    }

    

    @Test
    void getRosterRules_returnsCorrectValues() {
        assertEquals(18, sport.getRosterRules().getRosterSize());
        assertEquals(7,  sport.getRosterRules().getBenchSize());
        assertEquals(11, sport.getRosterRules().getStartingLineupSize());
    }

    @Test
    void getScoringConfig_returnsCorrectValues() {
        assertEquals(3, sport.getScoringConfig().getWinPoints());
        assertEquals(1, sport.getScoringConfig().getDrawPoints());
        assertEquals(0, sport.getScoringConfig().getLosePoints());
    }

   

    private List<Player> healthyPlayers(int count) {
        List<Player> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            list.add(new Player("Player" + i, "MID", 22, i + 1));
        }
        return list;
    }
}