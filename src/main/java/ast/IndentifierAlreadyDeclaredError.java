package ast;

import error.CompileError;
import lexer.Token;

public class IndentifierAlreadyDeclaredError extends CompileError {
  /**
   * @param ident
   * @param line
   * @param linePos
   */
  public IndentifierAlreadyDeclaredError(String ident, int line, int linePos) {
    super(String.format("Identifier already declared: %s", ident), line, linePos);
  }

  public IndentifierAlreadyDeclaredError(Token t) {
    this(t.lexem, t.line, t.linePos);
  }
}
