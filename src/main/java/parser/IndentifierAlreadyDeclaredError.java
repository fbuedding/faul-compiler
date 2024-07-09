package parser;

import error.CompileError;
import lexer.Token;

public class IndentifierAlreadyDeclaredError extends CompileError {
  public IndentifierAlreadyDeclaredError(Token t) {
    super(String.format("Identifier already declared: %s", t.lexem), t.line, t.linePos);
  }
}
