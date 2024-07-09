package error;

/**
 * CompileError
 */
public class CompileError extends Exception {
  public int line, linePos;

  public CompileError(String errorMessage, int line, int linePos) {
    super(String.format("%s, Position: %d:%d", errorMessage, line, linePos));
    this.line = line;
    this.linePos = linePos;
  }

}
