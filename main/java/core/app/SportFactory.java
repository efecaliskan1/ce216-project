package core.app;

import interfaces.ISport;
import sports.football.FootballSport;
import sports.volleyball.VolleyballSport;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class SportFactory {

    private static final Map<String, Supplier<ISport>> REGISTRY = new HashMap<>();

    static {
        REGISTRY.put("football",   FootballSport::new);
        REGISTRY.put("volleyball", VolleyballSport::new);
    }

    public static ISport create(String sportName) {
        Supplier<ISport> s = REGISTRY.get(sportName.toLowerCase());
        if (s == null) throw new IllegalArgumentException("Unknown sport: " + sportName);
        return s.get();
    }

    public static void register(String name, Supplier<ISport> supplier) {
        REGISTRY.put(name.toLowerCase(), supplier);
    }

    public static Set<String> getAvailableSports() { return REGISTRY.keySet(); }
    public static boolean     isRegistered(String name) { return REGISTRY.containsKey(name.toLowerCase()); }
}
