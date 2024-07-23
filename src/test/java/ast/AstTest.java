package ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import emitter.Emitter;
import error.CompileError;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenKind;
import parser.ParseTree;
import parser.Parser;
import types.TypeError;

/**
 * AstTest
 */
public class AstTest {

  @Test
  public void syntaxTree()
      throws CompileError, IOException {
    String i = """
        if(true){

        } else {
          int a = 0;
        }
        int a = -3 + 5;;;;;
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
          if((false && false) || (true || false)) {
            int asd = 0;
          }
        }
        int d = 5 % 3;
        int and = 5 & 5;
        int z1 = !(3-4);
        """; // */
    Lexer l = new Lexer(i);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, "", 0, 0));
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    file.Writer.write("src/test/resources/treeForAst.txt", pt.toString());
    try {
      AbstractSyntaxTree ast = astf.fromParseTree(pt);
      file.Writer.write("src/test/resources/Ast.txt", ast.toString());

    } catch (CompileError e) {
      fail(e);
    }

  }

  @Test
  public void functionTests() {
    String i = """
        int a =readI();
        int b = 3 * (5 - readI());
        print(a);
        exit();
        """;
    Lexer l = new Lexer(i);
    Parser p;
    try {
      p = new Parser(l.genTokens());
      ParseTree pt = new ParseTree(new Token(TokenKind.PROGRAM, "", 0, 0));
      p.program(pt);
      AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();
      AbstractSyntaxTree ast = astf.fromParseTree(pt);
      file.Writer.write("src/test/resources/functionAST.txt", ast.toString());

    } catch (CompileError e) {
      fail(e.getMessage(), e);
    } catch (IOException e) {
      fail("Could not write file");
    }

  }

  @ParameterizedTest
  @MethodSource("semanticErrorTestCases")
  public void testSemanticError(String input, boolean shouldError)
      throws IOException, InterruptedException, CompileError {
    Lexer l = new Lexer(input);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    try {
      astf.fromParseTree(pt);
    } catch (TypeError e) {
      if (!shouldError) {
        fail(e);
      }
    } catch (UnknownIdentifierError e) {
      if (!shouldError) {
        fail(e);
      }
    } catch (IndentifierAlreadyDeclaredError e) {
      if (!shouldError) {
        fail(e);
      }
    }
  }

  private static Stream<Arguments> semanticErrorTestCases() {
    return Stream.of(
        Arguments.of("""
            int i = (5 - 3) * -4 / (2 - 4);
            print(i);
            """, false),
        Arguments.of("""
            i = (5 - 3) * -4 / (2 - 4);
            print(i);
            """, true),
        Arguments.of("""
            int i = (5 - 3) * -4 / (2 - 4);
            int i = (5 - 3) * -4 / (2 - 4);
            """, true),
        Arguments.of("""
            int i = (5 - 3) * -4 / (2 - readI());
            print(i);
            """, false),
        Arguments.of("""
            bool i = (5 - 3) * -4 / (2 - !readI()) == 9 == !readB();
            print(i);
            """, false),
        Arguments.of("""
            bool i = (5 - 3) * -4 / (2 - 4) <= 0;
            print(i);
            """, false),
        Arguments.of("""
            int i = !true;
            print(i);
            """, true),
        Arguments.of("""
            int i = !1;
            i();
            """, true),
        Arguments.of("""
            int a = 7 == 8;
            a = true == false;
            int b = 7 != 8;
            a = true != false;
            """, true),
        Arguments.of("""
            bool a = 7 == 8;
            a = true == false;
            bool b = 7 != 8;
            a = true != false;
            """, false));
  }

}
