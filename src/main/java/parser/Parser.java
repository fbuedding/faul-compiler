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

  private boolean checkToken(TokenType tt) {
    return tokens[currentToken].kind == tt;
  }

  private boolean checkPeekToken(TokenType tt) {
    return tokens[nextToken].kind == tt;
  }

  private void matchToken(TokenType tt) throws SyntaxError {
    if (!checkToken(tt)) {
      throw new SyntaxError(currentToken());
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
   * 
   * @throws SyntaxError*
   */
  public void program(SyntaxTree st) throws SyntaxError {

    while (!checkToken(TokenType.EOF)) {
      this.statement(st.insertSubtree(new Token(TokenType.STATEMENT, "")));
    }
  }

  /**
   * <statement>::= "int" <ident> "=" <arithmeticExpr> <semi>
   * | "bool" <ident> "=" <logicalExpr> <semi>
   * | "if" "(" <condition> ")" "{" <statement>* "}"
   * | <ident> "=" (<logicalExpr> | <arithmeticExpr>) <semi>
   * 
   * @param st
   * @throws SyntaxError
   */
  private void statement(SyntaxTree st) throws SyntaxError {
    TokenType[] expected = new TokenType[] { TokenType.INT, TokenType.BOOL, TokenType.IF, TokenType.IDENT };
    switch (currentToken().kind) {
      case INT:
        st.insertSubtree(currentToken());
        nextToken();
        st.insertSubtree(currentToken());
        matchToken(TokenType.IDENT);
        st.insertSubtree(currentToken());
        matchToken(TokenType.EQ);
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        break;
      case BOOL:
        break;
      case IF:
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
    return;
  }

  private void arithmeticExpr(SyntaxTree st) throws SyntaxError {
    term(st.insertSubtree(new Token(TokenType.TERM, "")));
    if (checkPeekToken(TokenType.PLUS)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.PLUS);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkPeekToken(TokenType.PLUS)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.PLUS);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    }
  }

  private void term(SyntaxTree st) throws SyntaxError {
    unary(st.insertSubtree(new Token(TokenType.UNARY, "")));
    if (checkPeekToken(TokenType.ASTERISK)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.ASTERISK);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkPeekToken(TokenType.SLASH)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.SLASH);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    }
  }

  private void unary(SyntaxTree st) throws SyntaxError {
    switch (currentToken().kind) {
      case OPEN_BRACKET:
        st.insertSubtree(currentToken());
        nextToken();
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        matchToken(TokenType.CLOSE_BRACKET);
        break;

      default:
        break;
    }
  }

}
