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

  private Token currentToken() {
    return tokens[currentToken];
  }

  // ################# Begin parsing methods #################

  /**
   * <program>::= <statement>
 * @throws SyntaxError*
   */
  public void program() throws SyntaxError {
    System.out.println("PROGRAMM");
    while (!checkToken(TokenType.EQ)) {
      this.statement();
    }
  }

  /**
   * <statement>::= "int" <ident> "=" <arithmeticExpr> <semi>
   *            | "bool" <ident> "=" <logicalExpr> <semi>
   *            | "if" "(" <condition> ")" "{" <statement>* "}"
   *            | <ident> "=" (<logicalExpr> | <arithmeticExpr>) <semi>
 * @throws SyntaxError
   */
  private void statement() throws SyntaxError {
    TokenType[] expected = new TokenType[]{TokenType.INT, TokenType.BOOL, TokenType.IF, TokenType.IDENT};
    switch (currentToken().kind){
      case INT:
        break;
      case BOOL:
        break;
      case IF:
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
  }

}
