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
        int a = 3 + 5;
        bool b = a < 6;
        int c = 5 * ( 5 + 6 * 3);
        bool e = true && false;
        bool g = 5 == 8 - 5*3;
        bool z = 4 >= 5*6;
        if(g && true){
          int d = 6;
          if(true) {
            z = 4 >= 5*6;
            int asd = 5;
          }
          if((false && false) || (true || false) | 6) {
            int asd = 5;

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
    file.Writer.write("src/test/resources/Ast.txt", ast.toString());

    System.out.println(astf.sTable);
  }

}
