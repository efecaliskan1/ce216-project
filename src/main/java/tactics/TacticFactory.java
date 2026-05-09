package tactics;

import interfaces.ITacticStrategy;

public class TacticFactory {

    public static ITacticStrategy create(String name) {
        return switch (name.toLowerCase()) {
            case "defensive"                  -> new DefensiveStrategy();
            case "blockfocus", "block_focus" -> new DefensiveStrategy();
            case "balanced"                   -> new BalancedStrategy();
            case "highpress", "high_press"    -> new HighPressStrategy();
            case "servepressure", "serve_pressure" -> new HighPressStrategy();
            case "counterattack","counter_attack" -> new CounterAttackStrategy();
            default -> throw new IllegalArgumentException("Unknown tactic: " + name);
        };
    }
}
