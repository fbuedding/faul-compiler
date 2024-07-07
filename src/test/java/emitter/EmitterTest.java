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

}
