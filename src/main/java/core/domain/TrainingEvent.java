package core.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrainingEvent implements Serializable {

    private final Coach coach;
    private final List<Player> players;
    private final TrainingPlan plan;
    private final Map<Player, Integer> result;

    public TrainingEvent(Coach coach, List<Player> players, TrainingPlan plan) {
        this.coach   = coach;
        this.players = players;
        this.plan    = plan;
        this.result  = new HashMap<>();
    }

    public void execute() {
        for (Player p : players) {
            if (p.isAvailable()) {
                int before = p.getAttribute(plan.getTargetAttribute());
                p.train(plan);
                int after  = p.getAttribute(plan.getTargetAttribute());
                result.put(p, after - before);
            }
        }
    }

    public Coach              getCoach()   { return coach; }
    public List<Player>       getPlayers() { return players; }
    public TrainingPlan       getPlan()    { return plan; }
    public Map<Player,Integer>getResult()  { return result; }
}
