package parser;

import lexer.Token;
import lexer.TokenType;

/**
 * 
 */
public class Parser {
  private Token[] tokens;
  private int currentToken = 0;
  private int nextToken = 1;

  public Parser(Token[] tokens) {
    this.tokens = tokens;
    if (tokens.length < 2)
      throw new Error("Empty File!");
  }

  public boolean checkToken(Token t) {
    return tokens[currentToken].kind == t.kind;
  }

  public boolean checkToken(TokenType tt) {
    return tokens[currentToken].kind == tt;
  }

  public boolean checkPeekToken(Token t) {
    return tokens[nextToken].kind == t.kind;
  }

  public void matchToken(Token t) throws SyntaxError {
    if (!checkToken(t)) {
      throw new SyntaxError(t);
    }
    nextToken();
  }

  private void nextToken() {
    currentToken = nextToken++;
  }

  // ################# Begin parsing methods #################

  /**
   * <program>::= <statement>*
   */
  public void program() {
    System.out.println("PROGRAMM");
    while (!checkToken(TokenType.EQ)) {
      this.statement();
    }
  }

private void statement() {
}

}
