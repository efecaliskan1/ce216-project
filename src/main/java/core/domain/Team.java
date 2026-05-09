package core.domain;

import interfaces.ITacticStrategy;
import valueobjects.RosterRules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Team implements Serializable {
    private static final Map<String, Integer> FOOTBALL_STARTER_SHAPE = new LinkedHashMap<>();
    static {
        FOOTBALL_STARTER_SHAPE.put("Goalkeeper", 1);
        FOOTBALL_STARTER_SHAPE.put("Defender", 4);
        FOOTBALL_STARTER_SHAPE.put("Midfielder", 4);
        FOOTBALL_STARTER_SHAPE.put("Striker", 2);
    }

    private String name;
    private String logo;
    private List<Player> players;
    private Coach coach;
    private ITacticStrategy currentTactic;
    private int substitutionsMadeThisMatch;
    private RosterRules rosterRules;

    public Team(String name, String logo, RosterRules rosterRules) {
        this.name         = name;
        this.logo         = logo;
        this.rosterRules  = rosterRules;
        this.players      = new ArrayList<>();
    }

    public void setCurrentTactic(ITacticStrategy tactic) { this.currentTactic = tactic; }
    public ITacticStrategy getCurrentTactic()            { return currentTactic; }

    public boolean makeSub(Player out, Player in) {
        if (substitutionsMadeThisMatch >= rosterRules.getSubstitutionLimitPerMatch()) return false;
        if (!in.isAvailable()) return false;
        if (!getStartingLineup().contains(out)) return false;
        int outIdx = players.indexOf(out);
        int inIdx  = players.indexOf(in);
        if (outIdx == -1 || inIdx == -1) return false;
        Collections.swap(players, outIdx, inIdx);
        substitutionsMadeThisMatch++;
        return true;
    }

    public boolean swapPositions(Player a, Player b) {
        int ai = players.indexOf(a);
        int bi = players.indexOf(b);
        if (ai == -1 || bi == -1 || ai == bi) return false;
        Collections.swap(players, ai, bi);
        return true;
    }

    public boolean swapStarterWithBench(Player starter, Player benchPlayer) {
        int sz = rosterRules.getStartingLineupSize();
        int starterIdx = players.indexOf(starter);
        int benchIdx = players.indexOf(benchPlayer);
        if (starterIdx == -1 || benchIdx == -1) return false;
        if (starterIdx >= sz) return false;
        if (benchIdx < sz) return false;
        if (!benchPlayer.isAvailable()) return false;
        if (usesFootballFormationRules()
                && !starter.getPosition().equalsIgnoreCase(benchPlayer.getPosition())) {
            return false;
        }
        Collections.swap(players, starterIdx, benchIdx);
        return true;
    }

    public Player findAvailableBenchReplacementFor(Player starter) {
        if (starter == null) return null;
        for (Player benchPlayer : getBench()) {
            if (benchPlayer.isAvailable() && starter.getPosition().equalsIgnoreCase(benchPlayer.getPosition())) {
                return benchPlayer;
            }
        }
        for (Player benchPlayer : getBench()) {
            if (benchPlayer.isAvailable()) return benchPlayer;
        }
        return null;
    }

    public boolean autoReplaceInjuredStarters() {
        boolean changed = false;
        for (Player starter : new ArrayList<>(getStartingLineup())) {
            if (!starter.isInjured()) continue;
            Player replacement = findAvailableBenchReplacementFor(starter);
            if (replacement == null) continue;
            if (swapStarterWithBench(starter, replacement)) {
                changed = true;
            }
        }
        return changed;
    }

    public boolean promoteToStarter(Player benchPlayer) {
        int sz = rosterRules.getStartingLineupSize();
        int idx = players.indexOf(benchPlayer);
        if (idx == -1 || idx < sz) return false;
        if (!benchPlayer.isAvailable()) return false;

        Collections.swap(players, sz - 1, idx);
        return true;
    }

    public boolean demoteToBench(Player starter) {
        int sz = rosterRules.getStartingLineupSize();
        int idx = players.indexOf(starter);
        if (idx == -1 || idx >= sz) return false;
        if (players.size() <= sz) return false;
        Collections.swap(players, idx, sz);
        return true;
    }

    public List<Player> getStartingLineup() {
        int sz = rosterRules.getStartingLineupSize();
        return new ArrayList<>(players.subList(0, Math.min(sz, players.size())));
    }

    public List<Player> getBench() {
        int sz = rosterRules.getStartingLineupSize();
        if (players.size() <= sz) return new ArrayList<>();
        return new ArrayList<>(players.subList(sz, players.size()));
    }

    public List<Player> getAvailablePlayers() {
        List<Player> out = new ArrayList<>();
        for (Player p : players) if (p.isAvailable()) out.add(p);
        return out;
    }

    public boolean validateLineup() {
        List<Player> lineup = getStartingLineup();
        if (lineup.size() != rosterRules.getStartingLineupSize()) return false;
        for (Player p : lineup) if (!p.isAvailable()) return false;
        if (usesFootballFormationRules() && !matchesFootballStarterShape(lineup)) return false;
        return true;
    }

    public boolean usesFootballFormationRules() {
        if (rosterRules.getRosterSize() != 25 || rosterRules.getBenchSize() != 14) return false;
        for (String position : FOOTBALL_STARTER_SHAPE.keySet()) {
            if (countPlayersByPosition(players, position) == 0) return false;
        }
        return true;
    }

    public void organizeFootballLineup() {
        if (!usesFootballFormationRules()) return;

        List<Player> reordered = new ArrayList<>(players.size());
        List<Player> leftovers = new ArrayList<>(players);

        for (Map.Entry<String, Integer> entry : FOOTBALL_STARTER_SHAPE.entrySet()) {
            movePlayersByPosition(leftovers, reordered, entry.getKey(), entry.getValue());
        }
        for (String position : FOOTBALL_STARTER_SHAPE.keySet()) {
            movePlayersByPosition(leftovers, reordered, position, Integer.MAX_VALUE);
        }
        reordered.addAll(leftovers);
        players.clear();
        players.addAll(reordered);
    }

    public void resetMatchState()  { substitutionsMadeThisMatch = 0; }
    public void addPlayer(Player p){ players.add(p); }

    public String       getName()                      { return name; }
    public String       getLogo()                      { return logo; }
    public List<Player> getPlayers()                   { return players; }
    public Coach        getCoach()                     { return coach; }
    public void         setCoach(Coach coach)          { this.coach = coach; }
    public int          getSubstitutionsMadeThisMatch(){ return substitutionsMadeThisMatch; }
    public RosterRules  getRosterRules()               { return rosterRules; }

    private boolean matchesFootballStarterShape(List<Player> lineup) {
        for (Map.Entry<String, Integer> entry : FOOTBALL_STARTER_SHAPE.entrySet()) {
            if (countPlayersByPosition(lineup, entry.getKey()) != entry.getValue()) return false;
        }
        return true;
    }

    private int countPlayersByPosition(List<Player> source, String position) {
        int count = 0;
        for (Player player : source) {
            if (position.equalsIgnoreCase(player.getPosition())) count++;
        }
        return count;
    }

    private void movePlayersByPosition(List<Player> source, List<Player> target, String position, int limit) {
        int moved = 0;
        for (int i = 0; i < source.size() && moved < limit; ) {
            Player player = source.get(i);
            if (position.equalsIgnoreCase(player.getPosition())) {
                target.add(player);
                source.remove(i);
                moved++;
                continue;
            }
            i++;
        }
    }
}
