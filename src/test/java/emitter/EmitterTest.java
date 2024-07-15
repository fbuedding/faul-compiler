package emitter;

import static org.junit.jupiter.api.Assertions.fail;

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
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, "", 0, 0));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
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
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();
    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
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
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
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
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
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
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
    emitter.generate();
    file.Writer.write("src/test/resources/asm/while.asm", emitter.code.toString());
  }

  @Test
  public void functionTests() {
    String i = """
        int a = readI();
        int b = readI();
        print(a * b);
        exit();
        """;
    Lexer l = new Lexer(i);
    Parser p;
    try {
      p = new Parser(l.genTokens());
      ParseTree pt = new ParseTree();
      p.program(pt);
      AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();
      AbstractSyntaxTree ast = astf.fromParseTree(pt);
      Emitter emitter = new Emitter(ast, astf.symbolTable);
      emitter.generate();
      file.Writer.write("src/test/resources/asm/functions.asm", emitter.code.toString());

    } catch (CompileError e) {
      fail(e.getMessage(), e);
    } catch (IOException e) {
      fail("Could not write file");
    }

  }

  @Test
  public void littleCalcTest() {
    String i = """
        int a = 0;
        int b = 0;
        int erg = 0;
        while(readB()){
         int option = readI();
         a = readI();
         b = readI();

         if(option == 0){
          erg = a + b;
         }
         if(option == 1){
          erg = a - b;
         }
         if(option == 2){
          erg = a * b;
         }
         if(option == 4){
           if(a == 0){
            exit();
           }
          erg = a / b;
         }
         if(option == 5){
           if(a == 0){
            exit();
           }
          erg = a % b;
         }
         print(erg);
        }
        exit();
        """;
    Lexer l = new Lexer(i);
    Parser p;
    try {
      p = new Parser(l.genTokens());
      ParseTree pt = new ParseTree();
      p.program(pt);
      AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();
      AbstractSyntaxTree ast = astf.fromParseTree(pt);
      Emitter emitter = new Emitter(ast, astf.symbolTable);
      emitter.generate();
      file.Writer.write("src/test/resources/asm/calc.asm", emitter.code.toString());

    } catch (CompileError e) {
      fail(e.getMessage(), e);
    } catch (IOException e) {
      fail("Could not write file");
    }

  }
}
