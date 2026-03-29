package core.domain;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TrainingEvent {
    private final Coach coach;
    private final List<Player> players;
    private final TrainingPlan plan;
    private final Map<Player, Integer> result;

    public TrainingEvent(Coach coach, List<Player> players, TrainingPlan plan) {
        this.coach = coach;
        this.players = players == null ? Collections.<Player>emptyList() : List.copyOf(players);
        this.plan = plan;
        this.result = new LinkedHashMap<>();
    }

    public void execute() {
        if (plan == null) {
            return;
        }

        for (Player player : players) {
            if (player == null || !player.isAvailable()) {
                continue;
            }

            int before = player.getAttribute(plan.getTargetAttribute());
            player.train(plan);
            int after = player.getAttribute(plan.getTargetAttribute());
            result.put(player, after - before);
        }
    }

    public Coach getCoach() {
        return coach;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public TrainingPlan getPlan() {
        return plan;
    }

    public Map<Player, Integer> getResult() {
        return Collections.unmodifiableMap(result);
    }
}
