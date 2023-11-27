package parser;


import java.io.IOException;

import org.junit.jupiter.api.Test;

import lexer.Lexer;
import lexer.LexerError;
import lexer.Token;
import lexer.TokenType;

public class ParserTest {

  @Test
  public void syntaxTree() throws LexerError, SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError, IOException {
    String i = "int a = 3 + 5; int b = 4 - 6; int c = 5 * ( 5 + 6 * 3);";
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    SyntaxTree st = new SyntaxTree(new Token(TokenType.PROGRAM, ""));
    try {
      p.program(st);
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    file.Writer.write("src/test/resources/tree.txt", st.toString());
    System.out.println(st.toString());
  }
}
