package fsm;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class FsmTest {
  @Test
  public void shouldNotError() {
    assertDoesNotThrow(() -> {
      Fsm<Character> tmp = Fsm.integerFsm();
    });
  }

}
