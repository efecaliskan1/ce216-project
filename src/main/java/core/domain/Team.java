package core.domain;

import interfaces.ITacticStrategy;
import valueobjects.RosterRules;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Team implements Serializable {

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
        return true;
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
}
