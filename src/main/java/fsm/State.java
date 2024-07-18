package fsm;

import java.util.Vector;

/**
 * State
 * 
 * @todo Implement
 */
public class State<T> {
  public static class Builder<T> {
    Vector<Transition<T>> transitions = new Vector<Transition<T>>();

    public Builder() {
    }

    public Builder<T> addTransition(T[] conditions, int pointer) {
      transitions.add(new Transition<T>(conditions, pointer));
      return this;
    }

    public State<T> build() {
      return new State<T>(transitions);
    }
  }

  private final Vector<Transition<T>> transitions;

  private State(Vector<Transition<T>> transitions) {
    this.transitions = transitions;
  }

  public int nextState(final T e) throws NoTransitionError {
    for (final Transition<T> t : transitions) {
      if (t.conditionsContain(e))
        return t.getPointer();
    }
    throw new NoTransitionError();
  }
}
