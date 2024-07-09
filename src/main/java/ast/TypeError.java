package ast;

import error.CompileError;

/**
 * 
 */

public class TypeError extends CompileError {

  public TypeError(AstNodeTypes expected, AstNodeTypes got, int line, int linePos) {
    super(String.format("TypeError: expected: %s, got: %s", expected, got), line, linePos);
  }

}
