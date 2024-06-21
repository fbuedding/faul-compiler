package ast;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import lexer.Lexer;
import lexer.LexerError;
import lexer.Token;
import lexer.TokenKind;
import parser.IndentifierAlreadyDeclaredError;
import parser.Parser;
import parser.SyntaxError;
import parser.ParseTree;
import parser.UnknownIdentifierError;

/**
 * AstTest
 */
public class AstTest {

  @Test
  public void syntaxTree()
      throws LexerError, SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError, IOException {
    String i = """
        int a = 3 + -5;
        int b = a - 6;
        int c = 5 * ( 5 + 6 * 3);
        bool g = 5 == 8 - 5*3;
        bool z = 4 >= 5*6;
        """;
        /*
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
        """; //*/
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree st = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(st);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();
    
    file.Writer.write("src/test/resources/treeForAst.txt", st.toString());
    AbstractSyntaxTree ast = astf.fromParseTree(st);
    System.out.println(ast);
    System.out.println(astf.sTable);
  }

}
