package lexer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class LexerTest {
  @Test
  public void testCharacterReading() {
    String i = "Test";
    StringBuilder erg = new StringBuilder();
    Lexer l = new Lexer(i);
    while (l.lookAhead() != '\0') {
      erg.append(l.getCurrentCharacter());
      l.nextCharacter();
    }
    assertEquals(i, erg.toString());
  }

  @Test
  public void shouldNotThrow() {
    assertDoesNotThrow(() -> {
      String i = "+-*/";
      Lexer l = new Lexer(i);
      Token token = l.getToken();
      while (token.kind != TokenType.EOF) {
        token = l.getToken();
      }
    });
  }

  @Test
  public void shouldThrow() {
    assertThrows(LexerError.class, () -> {
      String i = "🫥";
      Lexer l = new Lexer(i);
      Token token = l.getToken();
      while (token.kind != TokenType.EOF) {
        token = l.getToken();
      }
    });
  }

  @Test
  public void typeShouldMatch() throws LexerError {
    Map<String, TokenType> tests = new HashMap<String, TokenType>();
    tests.put("==", TokenType.EQEQ);
    tests.put("=", TokenType.EQ);
    tests.put("!=", TokenType.NOTEQ);
    tests.put("!", TokenType.NOT);
    tests.put("<=", TokenType.LTEQ);
    tests.put("<", TokenType.LT);
    tests.put(">=", TokenType.GTEQ);
    tests.put(">", TokenType.GT);
    for (Map.Entry<String, TokenType> test : tests.entrySet()) {
      Lexer l = new Lexer(test.getKey());
      assertEquals(l.getToken().kind, test.getValue());
    }
  }
}
