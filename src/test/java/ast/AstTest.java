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
        int a = -3 + 5;
        bool b = !true;
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
          else {
            int paul = 0;
            while(true){
              int fabian = 0;
            }
          }
          if((false && false) || (true || false) | 6) {
            int asd = 5;
          }
        }
        int d = 5 % 3;
        int and = 5 & 5;
        int z1 = !(3-4);
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    file.Writer.write("src/test/resources/treeForAst.txt", pt.toString());
    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    file.Writer.write("src/test/resources/Ast.txt", ast.toString());

  }

}
