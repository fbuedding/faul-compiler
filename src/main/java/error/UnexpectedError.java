package error;

/**
 * UnexpectedError
 */
public class UnexpectedError extends CompileError {
  public UnexpectedError(String message, int line, int linePos) {
    super(String.format("Unexpected error: %s", message), line, linePos);
  }
}
