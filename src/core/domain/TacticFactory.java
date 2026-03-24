package tactics;

import interfaces.ITacticStrategy;

public class TacticFactory {

    public static ITacticStrategy create(String name) {
        return switch (name.toLowerCase()) {
            case "defensive"                  -> new DefensiveStrategy();
            case "balanced"                   -> new BalancedStrategy();
            case "highpress", "high_press"    -> new HighPressStrategy();
            case "counterattack","counter_attack" -> new CounterAttackStrategy();
            default -> throw new IllegalArgumentException("Unknown tactic: " + name);
        };
    }
}
