package fsm;

import java.util.Vector;

/**
 * FSM
 * 
 * @todo Implement
 */
public class Fsm<T> {
  public static class Builder<T> {
    Vector<State<T>> states = new Vector<State<T>>();
    private int startState;
    private int endState;

    public Builder(int startState, int endState) {
      this.startState = startState;
      this.endState = endState;
    }

    public Builder<T> addState(State<T> s) {
      states.add(s);
      return this;
    }

    public Fsm<T> build() {
      return new Fsm<T>(states, startState, endState);
    }
  }

  private Vector<State<T>> states;
  private int currentState;
  private int startState;
  private int endState;

  private Fsm(Vector<State<T>> states, int startState, int endState) {
    this.startState = startState;
    this.currentState = this.startState;
    this.endState = endState;
    this.states = states;
  }

  public void reset() {
    this.currentState = this.startState;
  }

  public void nextState(T t) throws NoTransitionError {
    currentState = states.get(currentState).nextState(t);
  }

  public boolean isEndstate() {
    return currentState == endState;
  }

  public static Fsm<Character> integerFsm() {
    return new Fsm.Builder<Character>(0, 2)
        .addState(new State.Builder<Character>()
            .addTransition(new Character[] { '1', '2', '3', '4', '5', '6', '7', '8', '9' }, 1)
            .build())
        .addState(new State.Builder<Character>()
            .addTransition(new Character[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' }, 1)
            .addTransition(new Character[] { '-', '+', '*', '/', '<', '>', '!', ')', ';','=' }, 2)
            .build())
        .addState(new State.Builder<Character>()
            .build())
        .build();
  }

  public static Fsm<Character> wordFsm() {
    return new Fsm.Builder<Character>(0, 2)
        .addState(new State.Builder<Character>()
            .addTransition("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_".chars().mapToObj(c -> (char) c)
                .toArray(Character[]::new), 1)
            .build())
        .addState(new State.Builder<Character>()
            .addTransition(
                "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_1234567890".chars().mapToObj(c -> (char) c)
                    .toArray(Character[]::new),
                1)
            .addTransition(new Character[] {' ', '-', '+', '*', '/', '<', '>', '!', ')','(', ';','=' }, 2)
            .build())
        .addState(new State.Builder<Character>()
            .build())
        .build();
  }
}
