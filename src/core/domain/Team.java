package core.domain;

import interfaces.ITacticStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Team {
    private final String name;
    private final String logo;
    private final List<Player> players;
    private Coach coach;
    private final RosterRules rosterRules;
    private ITacticStrategy currentTactic;
    private int substitutionsMadeThisMatch;

    public Team(String name, String logo, RosterRules rosterRules) {
        this(name, logo, Collections.<Player>emptyList(), null, null, rosterRules);
    }

    public Team(
            String name,
            String logo,
            List<Player> players,
            Coach coach,
            ITacticStrategy currentTactic,
            RosterRules rosterRules
    ) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (rosterRules == null) {
            throw new IllegalArgumentException("rosterRules cannot be null");
        }

        this.rosterRules = rosterRules;
        List<Player> roster = new ArrayList<>(players == null ? Collections.<Player>emptyList() : players);
        if (roster.size() > this.rosterRules.getRosterSize()) {
            throw new IllegalArgumentException("player count cannot exceed roster size");
        }

        this.name = name;
        this.logo = logo;
        this.players = roster;
        this.coach = coach;
        this.currentTactic = currentTactic;
        this.substitutionsMadeThisMatch = 0;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Coach getCoach() {
        return coach;
    }

    public void setCoach(Coach coach) {
        this.coach = coach;
    }

    public RosterRules getRosterRules() {
        return rosterRules;
    }

    public void setCurrentTactic(ITacticStrategy tactic) {
        this.currentTactic = tactic;
    }

    public ITacticStrategy getCurrentTactic() {
        return currentTactic;
    }

    public int getSubstitutionsMadeThisMatch() {
        return substitutionsMadeThisMatch;
    }

    public boolean makeSub(Player out, Player in) {
        if (out == null || in == null) {
            return false;
        }
        if (substitutionsMadeThisMatch >= rosterRules.getSubstitutionLimitPerMatch()) {
            return false;
        }
        if (!in.isAvailable()) {
            return false;
        }
        if (out.equals(in)) {
            return false;
        }

        int lineupIndex = players.indexOf(out);
        int benchIndex = players.indexOf(in);
        int lineupSize = getStartingLineupSize();

        if (lineupIndex < 0 || lineupIndex >= lineupSize) {
            return false;
        }
        if (benchIndex < lineupSize) {
            return false;
        }

        Collections.swap(players, lineupIndex, benchIndex);
        substitutionsMadeThisMatch++;
        return true;
    }

    public List<Player> getStartingLineup() {
        int lineupSize = Math.min(getStartingLineupSize(), players.size());
        return Collections.unmodifiableList(new ArrayList<>(players.subList(0, lineupSize)));
    }

    public List<Player> getBench() {
        int lineupSize = Math.min(getStartingLineupSize(), players.size());
        if (lineupSize >= players.size()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(players.subList(lineupSize, players.size())));
    }

    public List<Player> getAvailablePlayers() {
        List<Player> availablePlayers = players.stream()
                .filter(Objects::nonNull)
                .filter(Player::isAvailable)
                .collect(Collectors.toList());
        return Collections.unmodifiableList(availablePlayers);
    }

    public boolean validateLineup() {
        List<Player> lineup = getStartingLineup();
        if (lineup.size() != getStartingLineupSize()) {
            return false;
        }

        for (Player player : lineup) {
            if (player == null || !player.isAvailable()) {
                return false;
            }
        }
        return true;
    }

    public void resetMatchState() {
        substitutionsMadeThisMatch = 0;
    }

    public void addPlayer(Player player) {
        if (player == null) {
            return;
        }
        if (players.size() >= rosterRules.getRosterSize()) {
            throw new IllegalStateException("roster is full");
        }
        players.add(player);
    }

    private int getStartingLineupSize() {
        return rosterRules.getRosterSize() - rosterRules.getBenchSize();
    }
}
