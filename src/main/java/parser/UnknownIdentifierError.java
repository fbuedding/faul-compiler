package parser;

import error.CompileError;
import lexer.Token;

public class UnknownIdentifierError extends CompileError {

  public UnknownIdentifierError(Token t) {
    super(String.format("Unknown Identifier: %s", t.lexem), t.line, t.linePos);
  }

}
