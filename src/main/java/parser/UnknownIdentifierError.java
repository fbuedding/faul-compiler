package parser;

import lexer.Token;

public class UnknownIdentifierError extends Exception {

  public UnknownIdentifierError(Token t) {
    super(String.format("Unknown Identifier: %s, Line: %d, Position: %d", t.lexem, t.line, t.linePos));
  }

}
