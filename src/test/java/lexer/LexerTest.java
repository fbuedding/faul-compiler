package lexer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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

  @Test
  public void shouldBeV_INT() throws LexerError {
    Token expected = new Token(TokenType.V_INT, "123456");
    Lexer l = new Lexer(expected.lexem + "+");
    Token is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + "-");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + "*");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + "/");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + "<");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + ">");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + "!");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + ")");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer(expected.lexem + ";");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    l = new Lexer("  " + expected.lexem + ";");
    is = l.getToken();
    assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
  }

  @Test
  public void shouldErrorV_INT() throws LexerError {
    String[] inputs = new String[] {
        "01234",
        "12a34",
        "12(34",
    };
    for (String input : inputs) {
      LexerError thrown = assertThrows(LexerError.class, () -> {
        Lexer l = new Lexer(input);
        l.getToken();
      });
      System.out.println(thrown.toString());
    }
  }

  @Test
  public void shouldReadIDENT() throws LexerError {
    String[] inputs = new String[] {
        "Fabian;",
        "Paul ",
        "Fabian1<",
        "_fabian123;",
    };
    for (String input : inputs) {
      Lexer l = new Lexer(input);
      Token is = l.getToken();
      Token expected = new Token(TokenType.IDENT, input.substring(0, input.length() - 1));
      assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    }
  }

  @Test
  public void testVaraibleAssignment() throws LexerError {
    String input = "int testInt = 123;";
    Token[] expected = new Token[] { 
      new Token(TokenType.INT, "int"),
      new Token(TokenType.IDENT, "testInt"),
      new Token(TokenType.EQ, "="),
      new Token(TokenType.V_INT, "123"),
      new Token(TokenType.SEMICOLON, ";"),
    };
    Lexer l = new Lexer(input);
    Token t = l.getToken();
    Vector<Token> tokens = new Vector<Token>();
    while (t.kind != TokenType.EOF) {
     tokens.add(t);
     t = l.getToken();
    }
    for(int i = 0; i< expected.length; i++ ){
      assertTrue(expected[i].equals(tokens.get(i)), String.format("Expected: %s but is %s", expected[i], tokens.get(i)));
    }
    System.out.println(tokens.toString());
  }
  @Test
  public void testIfBlock() throws LexerError {
    String input = "if( a == bc){\nint testInt=12 3 ;\nint a=123;\n}";
    Token[] expected = new Token[] { 
      new Token(TokenType.IF, "if"),
      new Token(TokenType.OPEN_BRACKET, "("),
      new Token(TokenType.IDENT, "a"),
      new Token(TokenType.EQEQ, "=="),
      new Token(TokenType.IDENT, "bc"),
      new Token(TokenType.CLOSE_BRACKET, ")"),
      new Token(TokenType.OPEN_PARANTHESES, "{"),
      new Token(TokenType.INT, "int"),
      new Token(TokenType.IDENT, "testInt"),
      new Token(TokenType.EQ, "="),
      new Token(TokenType.V_INT, "123"),
      new Token(TokenType.SEMICOLON, ";"),
      new Token(TokenType.INT, "int"),
      new Token(TokenType.IDENT, "a"),
      new Token(TokenType.EQ, "="),
      new Token(TokenType.V_INT, "123"),
      new Token(TokenType.SEMICOLON, ";"),
      new Token(TokenType.CLOSE_PARANTHESES, "}"),
    };
    Lexer l = new Lexer(input);
    Token t = l.getToken();
    Vector<Token> tokens = new Vector<Token>();
    while (t.kind != TokenType.EOF) {
     tokens.add(t);
     t = l.getToken();
    }
    System.out.println(tokens.toString());
    for(int i = 0; i< expected.length; i++ ){
      assertTrue(expected[i].equals(tokens.get(i)), String.format("Expected: %s but is %s", expected[i], tokens.get(i)));
    }
  }
}
