package lexer;

import error.CompileError;

public class LexerError extends CompileError {

  public LexerError(String errorMessage, int line, int linePos) {
    super(String.format("Lexical error: %s", errorMessage), line, linePos);
  }
}
