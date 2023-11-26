package fsm;


public class Transition<T> {
  private T[] conditions;
  private int pointer;

  public Transition(T[] conditions, int pointer) {
    this.conditions = conditions;
    this.pointer = pointer;
  }

  public int getPointer() {
    return pointer;
  }

  public boolean conditionsContain(T c) {
    for (T e : conditions) {
      if (e.equals(c))
        return true;
    }
    return false;
  }
}
