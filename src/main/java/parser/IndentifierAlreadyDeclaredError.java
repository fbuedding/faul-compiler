package parser;

import lexer.Token;

public class IndentifierAlreadyDeclaredError extends Exception {
  public IndentifierAlreadyDeclaredError(Token t) {
    super(String.format("Identifier already declared: %s, Line: %d, Position: %d", t.lexem, t.line, t.linePos));
  }
}
