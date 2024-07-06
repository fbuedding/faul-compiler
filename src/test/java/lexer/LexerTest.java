package lexer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.util.Arrays;
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
      while (token.kind != TokenKind.EOF) {
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
      while (token.kind != TokenKind.EOF) {
        token = l.getToken();
      }
    });
  }

  @Test
  public void typeShouldMatch() throws LexerError {
    Map<String, TokenKind> tests = new HashMap<String, TokenKind>();
    tests.put("==", TokenKind.EQEQ);
    tests.put("=", TokenKind.EQ);
    tests.put("!=", TokenKind.NOTEQ);
    tests.put("!", TokenKind.NOT);
    tests.put("<=", TokenKind.LTEQ);
    tests.put("<", TokenKind.LT);
    tests.put(">=", TokenKind.GTEQ);
    tests.put(">", TokenKind.GT);
    for (Map.Entry<String, TokenKind> test : tests.entrySet()) {
      Lexer l = new Lexer(test.getKey());
      assertEquals(l.getToken().kind, test.getValue());
    }
  }

  @Test
  public void shouldBeV_INT() throws LexerError {
    Token expected = new Token(TokenKind.V_INT, "123456");
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
      Token expected = new Token(TokenKind.IDENT, input.substring(0, input.length() - 1));
      assertTrue(expected.equals(is), String.format("Expected: %s but is %s", expected, is));
    }
  }

  @Test
  public void testVaraibleAssignment() throws LexerError {
    String input = "int testInt = 123;";
    Token[] expected = new Token[] {
        new Token(TokenKind.INT, "int"),
        new Token(TokenKind.IDENT, "testInt"),
        new Token(TokenKind.EQ, "="),
        new Token(TokenKind.V_INT, "123"),
        new Token(TokenKind.SEMICOLON, ";"),
    };
    Lexer l = new Lexer(input);
    Token t = l.getToken();
    Vector<Token> tokens = new Vector<Token>();
    while (t.kind != TokenKind.EOF) {
      tokens.add(t);
      t = l.getToken();
    }
    for (int i = 0; i < expected.length; i++) {
      assertTrue(expected[i].equals(tokens.get(i)),
          String.format("Expected: %s but is %s", expected[i], tokens.get(i)));
    }
  }

  @Test
  public void testIfBlock() throws LexerError {
    String input = "if( a == bc){\nint testInt= 1 2 3 ;\nint a=12 3 ;\nbool testBool =true;\n} else{}";
    Token[] expected = new Token[] {
        new Token(TokenKind.IF, "if"),
        new Token(TokenKind.OPEN_BRACKET, "("),
        new Token(TokenKind.IDENT, "a"),
        new Token(TokenKind.EQEQ, "=="),
        new Token(TokenKind.IDENT, "bc"),
        new Token(TokenKind.CLOSE_BRACKET, ")"),
        new Token(TokenKind.OPEN_PARANTHESES, "{"),
        new Token(TokenKind.INT, "int"),
        new Token(TokenKind.IDENT, "testInt"),
        new Token(TokenKind.EQ, "="),
        new Token(TokenKind.V_INT, "123"),
        new Token(TokenKind.SEMICOLON, ";"),
        new Token(TokenKind.INT, "int"),
        new Token(TokenKind.IDENT, "a"),
        new Token(TokenKind.EQ, "="),
        new Token(TokenKind.V_INT, "123"),
        new Token(TokenKind.SEMICOLON, ";"),
        new Token(TokenKind.BOOL, "bool"),
        new Token(TokenKind.IDENT, "testBool"),
        new Token(TokenKind.EQ, "="),
        new Token(TokenKind.V_BOOL, "true"),
        new Token(TokenKind.SEMICOLON, ";"),
        new Token(TokenKind.CLOSE_PARANTHESES, "}"),
        new Token(TokenKind.ELSE, "else"),
        new Token(TokenKind.OPEN_PARANTHESES, "{"),
        new Token(TokenKind.CLOSE_PARANTHESES, "}"),
    };
    Lexer l = new Lexer(input);
    Token t = l.getToken();
    Vector<Token> tokens = new Vector<Token>();
    while (t.kind != TokenKind.EOF) {
      tokens.add(t);
      t = l.getToken();
    }
    for (int i = 0; i < expected.length; i++) {
      assertTrue(expected[i].equals(tokens.get(i)),
          String.format("Expected: %s but is %s", expected[i], tokens.get(i)));
    }
  }

  @Test
  public void testWhileLoop() throws LexerError {
    String input = "while(true){}";
    Token[] expected = new Token[] {
        new Token(TokenKind.WHILE, "while"),
        new Token(TokenKind.OPEN_BRACKET, "("),
        new Token(TokenKind.V_BOOL, "true"),
        new Token(TokenKind.CLOSE_BRACKET, ")"),
        new Token(TokenKind.OPEN_PARANTHESES, "{"),
        new Token(TokenKind.CLOSE_PARANTHESES, "}"),
    };
    Lexer l = new Lexer(input);
    Token t = l.getToken();
    Vector<Token> tokens = new Vector<Token>();
    while (t.kind != TokenKind.EOF) {
      tokens.add(t);
      t = l.getToken();
    }
    for (int i = 0; i < expected.length; i++) {
      assertTrue(expected[i].equals(tokens.get(i)),
          String.format("Expected: %s but is %s", expected[i], tokens.get(i)));
    }
  }

  @Test
  public void norErrorFile() throws FileNotFoundException {
    String input = file.Reader.readFile("src/test/resources/simpleNoError.faul");
    assertDoesNotThrow(() -> {
      Lexer l = new Lexer(input);
      l.genTokens();
    });
  }

  @Test
  public void shouldErrorOnLine3Pos10() throws FileNotFoundException {
    String input = file.Reader.readFile("src/test/resources/lexerErrorOnLine3Pos10.faul");
    LexerError thrown = assertThrows(LexerError.class, () -> {
      Lexer l = new Lexer(input);
      Token token = l.getToken();
      while (token.kind != TokenKind.EOF) {
        token = l.getToken();
      }
    });
    assertEquals(3, thrown.line);
    assertEquals(10, thrown.linePos);
  }
}
