package ast;

import error.CompileError;
import lexer.Token;

public class UnknownIdentifierError extends CompileError {
  public UnknownIdentifierError(Token t) {
    this(t.lexem, t.line, t.linePos);
  }

  public UnknownIdentifierError(String ident, int line, int linePos) {
    super(String.format("Unknown Identifier: %s", ident), line, linePos);
  }

}
