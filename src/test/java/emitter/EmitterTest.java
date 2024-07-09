package emitter;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ast.AbstractSyntaxTree;
import ast.AbstractSyntaxTreeFactory;
import error.CompileError;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenKind;
import parser.ParseTree;
import parser.Parser;

/**
 * EmitterTest
 */
public class EmitterTest {
  @Test
  public void shouldNotErrorTest()
      throws IOException, CompileError {
    String i = """
        int a = ( 8 / 4 ) * 3;
        int b = a * -a;
        int c = b % 5;
        bool d = !true;
        int e = 0;
        if(true){
          int f = 0;
          e=1;
        } else {
          e=2;
        }
        int f = 2;
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
      throws IOException, CompileError {
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
        if(a1 == 1){
          int a13 = 13;
          int a14 = 14;
          int a15 = 15;
        }
        int a13 = a1 + a2 + a3 + a4 + a5 + a6 + a7 + a8 + a9;
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

  @Test
  public void shouldEvalToTrue()
      throws IOException, CompileError {
    String i = """
        bool a = 6 <= 6;
        bool b = !!a;
        bool c = !(a != b);
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.sTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/shouldAllBeTrue.asm", emitter.code.toString());
  }

  @Test
  public void ifTests()
      throws IOException, CompileError {
    String i = """
        int a = 0;
        if(2 % 2 == 0){
          a = 2;
        }

        if(true) {
          int b = 1;
        } else {
          int b = 2;
        }
        int b = 3;
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.sTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/firstVarTwoSecondThree.asm", emitter.code.toString());
  }

  @Test
  public void whileTest()
      throws IOException, CompileError {
    String i = """
        int a = 1;
        int b = 0;
        while(a < 16) {
          a = a * 2;
          b = b + 1;
        }
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, ""));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.sTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/while.asm", emitter.code.toString());
  }
}
