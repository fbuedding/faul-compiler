package parser;

import java.util.Arrays;

import error.CompileError;
import lexer.Token;
import lexer.TokenKind;

public class SyntaxError extends CompileError {
  public Token t;

  public SyntaxError(Token t) {
    super(String.format("Syntax error parsing Token: %s", t), t.line, t.linePos);
    this.t = t;
  }

  public SyntaxError(Token t, TokenKind[] tt) {
    super(String.format("Syntax error parsing Token: %s, Expected: %s", t, Arrays.toString(tt)), t.line, t.linePos);
    this.t = t;
  }

  public SyntaxError(Token t, TokenKind tp) {
    super(String.format("Syntax error parsing Token: %s, Expected: %s", t, tp), t.line, t.linePos);
    this.t = t;
  }
}
