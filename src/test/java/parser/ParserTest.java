package parser;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import error.CompileError;
import lexer.Lexer;
import lexer.LexerError;
import lexer.Token;
import lexer.TokenKind;

public class ParserTest {

  @Test
  public void shouldNotError() {
    String i = """
        int a = 3 + 5;
        int b = a - 6;
        int c = 5 * ( 5 + 6 * 3);
        bool g = 5 <= -5;
        bool z = 4 >= 5*6;
        if(g){
          int d = 6;
          int e = d *( 5 -3 /7);
          if(true) {
            z = 4 >= 5*6;
          }
          if((false && false) || (true || false) | 6) {

          }
        }
        int d = 5;
        """;
    try {
      Lexer l = new Lexer(i);
      Parser p = new Parser(l.genTokens());
      ParseTree st = new ParseTree(new Token(TokenKind.PROGRAM, ""));
      p.program(st);
      file.Writer.write("src/test/resources/tree.txt", st.toString());

    } catch (CompileError e) {
      fail(e);
    } catch (IOException e) {
      fail("Couldn't write file");
    }

  }

  @Test
  public void functionTests() {
    String i = """
        int a = read();
        int b = 3 * (5 - read());
        print(a, 1, a);
        exit();
        """;
    Lexer l = new Lexer(i);
    Parser p;
    try {
      p = new Parser(l.genTokens());
      ParseTree st = new ParseTree(new Token(TokenKind.PROGRAM, ""));
      p.program(st);
      file.Writer.write("src/test/resources/functionParseTree.txt", st.toString());
    } catch (CompileError e) {
      fail(e);
    } catch (IOException e) {
      fail("Could not write file");
    }

  }
}
