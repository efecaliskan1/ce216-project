package observer;

import core.domain.MatchEvent;
import core.domain.MatchResult;
import interfaces.MatchObserver;

import java.util.function.Consumer;

public class UIMatchObserver implements MatchObserver {

    private Consumer<MatchEvent> onGoalCallback;
    private Consumer<MatchEvent> onInjuryCallback;
    private Consumer<MatchEvent> onSubstitutionCallback;
    private TriConsumer<Integer, Integer, Integer> onPeriodEndCallback;

    private Consumer<MatchResult> onMatchEndCallback;


    public void setOnGoal(Consumer<MatchEvent> cb)       
      { onGoalCallback = cb; }
    public void setOnInjury(Consumer<MatchEvent> cb)     
      { onInjuryCallback = cb; }
    public void setOnSubstitution(Consumer<MatchEvent> cb) 
    { onSubstitutionCallback = cb; }
    public void setOnPeriodEnd(TriConsumer<Integer,Integer,Integer> cb) 
    { onPeriodEndCallback = cb; }



    public void setOnMatchEnd(Consumer<MatchResult> cb)  
      { onMatchEndCallback = cb; }

    @Override public void onGoal(MatchEvent e)      
       { if (onGoalCallback != null)      
          onGoalCallback.accept(e); }
    @Override public void onInjury(MatchEvent e)     
      { if (onInjuryCallback != null)       onInjuryCallback.accept(e); }
    @Override public void onSubstitution(MatchEvent e) 
    { if (onSubstitutionCallback != null) onSubstitutionCallback.accept(e); }
    @Override public void onPeriodEnd(int p, int h, int a)


     { if (onPeriodEndCallback != null) onPeriodEndCallback.accept(p, h, a); }
    @Override public void onMatchEnd(MatchResult r)   
    
     { if (onMatchEndCallback != null)     onMatchEndCallback.accept(r); }

    @FunctionalInterface
    public interface TriConsumer<A, B, C> { void accept(A a, B b, C c); }
}
