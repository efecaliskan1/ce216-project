package core.services;

import core.domain.Coach;
import core.domain.Player;
import core.domain.Team;
import core.domain.TrainingPlan;
import interfaces.ISport;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlayerGenerator {

    private static final String[] FIRST = {
        "James","Luca","Marco","Pedro","Carlos","Ali","Omar","Kai",
        "Noah","Leon","Finn","Emil","Enzo","Ivan","Diego","Mateo"
    };
    private static final String[] LAST = {
        "Smith","Rossi","Silva","Garcia","Müller","Yilmaz","Hassan",
        "Kim","Santos","Fernandez","Becker","Costa","Lopez","Novak"
    };
    private static final String[] FOOTBALL_POSITIONS = {
        "Goalkeeper","Defender","Defender","Defender","Defender",
        "Midfielder","Midfielder","Midfielder",
        "Striker","Striker","Striker",
        "Defender","Midfielder","Midfielder","Striker","Defender","Midfielder","Striker"
    };
    private static final String[] FOOTBALL_ATTRS = {
        "finishing","passing","tackling","dribbling","pace","stamina","strength"
    };
    private static final String[] COACH_SPECS = {
        "finishing","passing","tackling","stamina"
    };

    private static final Random RNG = new Random();

    public static List<Player> generateForSport(ISport sport, Team team) {
        int size = sport.getRosterRules().getRosterSize();
        List<Player> players = new ArrayList<>();
        List<Integer> usedNums = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            String pos = FOOTBALL_POSITIONS[i % FOOTBALL_POSITIONS.length];
            int shirt   = uniqueShirt(usedNums);
            usedNums.add(shirt);
            Player p = new Player(randomName(), pos, generateAge(), shirt);
            for (String a : FOOTBALL_ATTRS) p.setAttribute(a, RNG.nextInt(100) + 1);
            players.add(p);
        }
        return players;
    }

    public static List<Team> generateTeams(ISport sport, List<String> names) {
        List<Team> teams = new ArrayList<>();
        for (String name : names) {
            Team t = new Team(name, name.toLowerCase().replace(" ","_") + ".png",
                              sport.getRosterRules());
            generateForSport(sport, t).forEach(t::addPlayer);
            t.setCoach(generateCoach());
            teams.add(t);
        }
        return teams;
    }

    public static Coach generateCoach() {
        String spec = COACH_SPECS[RNG.nextInt(COACH_SPECS.length)];
        Coach c = new Coach(randomName(), spec);
        c.addTrainingPlan(new TrainingPlan(spec, RNG.nextInt(5) + 1));
        return c;
    }

    public static int generateAge() { return 17 + RNG.nextInt(19); } // 17-35

    private static String randomName() {
        return FIRST[RNG.nextInt(FIRST.length)] + " " + LAST[RNG.nextInt(LAST.length)];
    }

    private static int uniqueShirt(List<Integer> used) {
        int n;
        do { n = 1 + RNG.nextInt(99); } while (used.contains(n));
        return n;
    }
}
