package core.services;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class NameDataService {

    private static final List<String> MALE_FIRST   = load("/data/male_first_names.csv");
    private static final List<String> FEMALE_FIRST = load("/data/female_first_names.csv");
    private static final List<String> LAST         = load("/data/last_names.csv");
    private static final List<String> TEAMS        = load("/data/team_names.csv");

    private NameDataService() {}

    public static String randomMaleName(Random rng) {
        return pick(MALE_FIRST, rng) + " " + pick(LAST, rng);
    }

    public static String randomFemaleName(Random rng) {
        return pick(FEMALE_FIRST, rng) + " " + pick(LAST, rng);
    }

    public static String randomPlayerName(Random rng) {
        return rng.nextInt(100) < 80 ? randomMaleName(rng) : randomFemaleName(rng);
    }

    public static String randomCoachName(Random rng) {
        return rng.nextBoolean() ? randomMaleName(rng) : randomFemaleName(rng);
    }

    public static List<String> pickTeamNames(int n, Random rng) {
        List<String> copy = new ArrayList<>(TEAMS);
        Collections.shuffle(copy, rng);
        return copy.subList(0, Math.min(n, copy.size()));
    }

    public static List<String> allTeamNames() {
        return Collections.unmodifiableList(TEAMS);
    }

    private static String pick(List<String> pool, Random rng) {
        if (pool.isEmpty()) return "Unknown";
        return pool.get(rng.nextInt(pool.size()));
    }

    private static List<String> load(String resourcePath) {
        List<String> out = new ArrayList<>();
        InputStream in = NameDataService.class.getResourceAsStream(resourcePath);
        if (in == null) {
            System.err.println("Missing resource: " + resourcePath + " (falling back to defaults)");
            return fallback(resourcePath);
        }
        try (BufferedReader r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = r.readLine()) != null) {
                for (String token : line.split(",")) {
                    String t = token.trim();
                    if (!t.isEmpty()) out.add(t);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to read " + resourcePath + ": " + e.getMessage());
            return fallback(resourcePath);
        }
        return out;
    }

    private static List<String> fallback(String kind) {
        if (kind.contains("team"))   return List.of("Red Lions","Blue Eagles","Green Wolves","Black Panthers");
        if (kind.contains("female")) return List.of("Anna","Maria","Elena","Sofia");
        if (kind.contains("male"))   return List.of("John","Alex","Marco","David");
        return List.of("Smith","Johnson","Silva","Yilmaz");
    }
}
