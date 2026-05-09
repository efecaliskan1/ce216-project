package core.services;

import core.domain.Coach;
import core.domain.Player;
import core.domain.Team;
import core.domain.TrainingPlan;
import interfaces.ISport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PlayerGenerator {

    private static final Random RNG = new Random();
    private static final Map<String, List<String>> POSITION_KEYS = new HashMap<>();

    static {
        POSITION_KEYS.put("Goalkeeper", Arrays.asList("passing", "tackling", "stamina", "strength"));
        POSITION_KEYS.put("Defender", Arrays.asList("tackling", "strength", "stamina", "passing"));
        POSITION_KEYS.put("Midfielder", Arrays.asList("passing", "dribbling", "stamina", "pace"));
        POSITION_KEYS.put("Striker", Arrays.asList("finishing", "dribbling", "pace", "strength"));
        POSITION_KEYS.put("Setter", Arrays.asList("serving", "receiving", "stamina", "jump"));
        POSITION_KEYS.put("Libero", Arrays.asList("receiving", "serving", "stamina", "blocking"));
        POSITION_KEYS.put("MiddleBlocker", Arrays.asList("blocking", "reach", "jump", "spiking"));
        POSITION_KEYS.put("OutsideHitter", Arrays.asList("spiking", "serving", "jump", "receiving"));
    }

    public static List<Player> generateForSport(ISport sport, Team team) {
        List<Player> raw = sport.generatePlayers(team);
        List<Player> named = new ArrayList<>();
        List<Integer> usedNums = new ArrayList<>();

        for (Player p : raw) {
            int shirt = uniqueShirt(usedNums);
            usedNums.add(shirt);
            Player renamed = new Player(NameDataService.randomPlayerName(RNG),
                                        p.getPosition(),
                                        p.getAge(),
                                        shirt);

            for (var entry : p.getAttributes().entrySet()) {
                renamed.setAttribute(entry.getKey(), entry.getValue());
            }
            renamed.setOverall(calculateOverall(renamed));
            named.add(renamed);
        }
        return named;
    }

    public static List<Team> generateTeams(ISport sport, List<String> names) {
        List<Team> teams = new ArrayList<>();
        for (String name : names) {
            Team t = new Team(name,
                              name.toLowerCase().replace(" ", "_") + ".png",
                              sport.getRosterRules());
            generateForSport(sport, t).forEach(t::addPlayer);
            t.organizeFootballLineup();
            t.setCoach(generateCoachFor(sport));
            teams.add(t);
        }
        return teams;
    }

    public static Coach generateCoachFor(ISport sport) {
        String[] specs = sport.getName().equalsIgnoreCase("Volleyball")
                ? new String[]{"spiking", "blocking", "serving", "receiving"}
                : new String[]{"finishing", "passing", "tackling", "stamina"};
        String spec = specs[RNG.nextInt(specs.length)];
        Coach c = new Coach(NameDataService.randomCoachName(RNG), spec);
        c.addTrainingPlan(new TrainingPlan(spec, RNG.nextInt(5) + 1));
        return c;
    }

    @Deprecated
    public static Coach generateCoach() {
        String[] specs = {"finishing", "passing", "tackling", "stamina"};
        String spec = specs[RNG.nextInt(specs.length)];
        Coach c = new Coach(NameDataService.randomCoachName(RNG), spec);
        c.addTrainingPlan(new TrainingPlan(spec, RNG.nextInt(5) + 1));
        return c;
    }

    public static int generateAge() { return 17 + RNG.nextInt(19); }

    private static int uniqueShirt(List<Integer> used) {
        int n;
        do { n = 1 + RNG.nextInt(99); } while (used.contains(n));
        return n;
    }

    public static void ensureOveralls(List<Team> teams) {
        for (Team team : teams) {
            for (Player player : team.getPlayers()) {
                if (player.getOverall() <= 0) {
                    player.setOverall(calculateOverall(player));
                }
            }
        }
    }

    private static int calculateOverall(Player player) {
        List<String> keys = POSITION_KEYS.get(player.getPosition());
        if (keys == null || keys.isEmpty()) {
            double avg = player.getAttributes().values().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(50.0);
            return clampOverall((int) Math.round(60 + avg * 0.3));
        }

        double weighted = 0.0;
        for (String key : keys) {
            weighted += player.getAttribute(key);
        }
        weighted /= keys.size();
        return clampOverall((int) Math.round(60 + weighted * 0.3));
    }

    private static int clampOverall(int overall) {
        return Math.max(60, Math.min(90, overall));
    }
}
