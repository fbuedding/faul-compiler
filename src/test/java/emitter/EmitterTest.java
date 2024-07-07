package emitter;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ast.AbstractSyntaxTree;
import ast.AbstractSyntaxTreeFactory;
import lexer.Lexer;
import lexer.LexerError;
import lexer.Token;
import lexer.TokenKind;
import parser.IndentifierAlreadyDeclaredError;
import parser.ParseTree;
import parser.Parser;
import parser.SyntaxError;
import parser.UnknownIdentifierError;

/**
 * EmitterTest
 */
public class EmitterTest {
  @Test
  public void emitTest()
      throws LexerError, SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError, IOException {
    String i = """
        int a = ( 8 / 4 ) * 3;
        int b = a * a;
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.sTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/test.asm", emitter.code.toString());

  }

  @Test
  public void loadingMoreVarsThenRegsTest()
      throws LexerError, SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError, IOException {
    String i = """
        int a1 =  1;
        int a2 =  2;
        int a3 =  3;
        int a4 =  4;
        int a5 =  5;
        int a6 =  6;
        int a7 =  7;
        int a8 =  8;
        int a9 =  9;
        int a10 = 10;
        int a11 = 11;
        int a12 = 12;
          """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.sTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/vars.asm", emitter.code.toString());

  }

}
