package fsm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

class FsmTest {
  @Test
  public void shouldNotError() {
    assertDoesNotThrow(() -> {
      Fsm<Character> tmp = Fsm.integerFsm();
      Fsm<Character> tmpWord = Fsm.wordFsm();
    });
  }

}
