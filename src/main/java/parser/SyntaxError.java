package parser;

import java.util.Arrays;

import lexer.Token;
import lexer.TokenKind;

public class SyntaxError extends Exception {
  public Token t;

  public SyntaxError(Token t) {
    super(String.format("Syntax error parsing Token: %s, Line: %d, Position: %d", t, t.line, t.linePos));
    this.t = t;
  }

  public SyntaxError(Token t, TokenKind[] tt) {
    super(String.format("Syntax error parsing Token: %s, Line: %d, Position: %d\nExpected: %s", t, t.line, t.linePos,
        Arrays.toString(tt)));
    this.t = t;
  }

  public SyntaxError(Token t, TokenKind tp) {
    super(String.format("Syntax error parsing Token: %s, Line: %d, Position: %d\nExpected: %s", t, t.line, t.linePos,
        tp));
    this.t = t;
  }
}
