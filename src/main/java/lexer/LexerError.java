package lexer;

class LexerError extends Exception {
  public int line;
  public int linePos;

  public LexerError(String errorMessage, int line, int linePos) {
    super(String.format("Lexical error: %s, at line %d, position %d", errorMessage, line, linePos));
    this.line = line;
    this.linePos = linePos;
  }
}
