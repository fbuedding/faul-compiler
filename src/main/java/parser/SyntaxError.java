package parser;

import lexer.Token;

public class SyntaxError extends Exception {
  public Token t;

  public SyntaxError(Token t) {
    super(String.format("Syntax error parsing Token: %s, Line: %d, Position: %d", t, t.line, t.linePos));
    this.t = t;
  }
}
