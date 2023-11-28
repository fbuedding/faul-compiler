package parser;

import java.util.Vector;

import lexer.Token;
import lexer.TokenType;

/**
 * 
 */
public class Parser {
  private Token[] tokens;
  private int currentToken = 0;
  private int nextToken = 1;

  private Vector<String> declaredIdentifier = new Vector<String>();

  public Parser(Token[] tokens) {
    this.tokens = tokens;
    if (tokens.length < 2)
      throw new Error("Empty File!");
  }

  private boolean checkToken(TokenType tt) {
    return tokens[currentToken].kind == tt;
  }

  private void matchToken(TokenType tt, SyntaxTree st) throws SyntaxError {
    if (!checkToken(tt)) {
      throw new SyntaxError(currentToken(), tt);
    }
    st.insertSubtree(currentToken());
    nextToken();
  }

  private void nextToken() {
    currentToken = nextToken++;
  }

  private Token currentToken() {
    return tokens[currentToken];
  }
  private Token peekToken() {
    if (nextToken >= tokens.length) {
      return tokens[tokens.length-1]; //EOF
    }
    return tokens[nextToken];
  }

  // ################# Begin parsing methods #################

  /**
   * {@summary}<program>::= <statement>
   * 
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   * @throws IndentifierAlreadyDeclaredError*
   */
  public void program(SyntaxTree st) throws SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError {

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
   * @throws UnknownIdentifierError
   * @throws Indentifie rAlreadyDeclaredError
   */
  private void statement(SyntaxTree st) throws SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError {
    TokenType[] expected = new TokenType[] { TokenType.INT, TokenType.BOOL, TokenType.IF, TokenType.IDENT };
    switch (currentToken().kind) {
      case INT:
        matchToken(TokenType.INT, st);
        String ident = currentToken().lexem;
        if (declaredIdentifier.contains(ident)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenType.IDENT, st);
        matchToken(TokenType.EQ, st);
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        matchToken(TokenType.SEMICOLON, st);
        declaredIdentifier.add(ident);
        break;
      case BOOL:
        matchToken(TokenType.BOOL, st);
        String boolIdent = currentToken().lexem;
        if (declaredIdentifier.contains(boolIdent)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenType.IDENT, st);
        matchToken(TokenType.EQ, st);
        logicalExpr(st.insertSubtree(new Token(TokenType.LOGICAL_EXPR, "")));
        matchToken(TokenType.SEMICOLON, st);
        declaredIdentifier.add(boolIdent);
        break;
      case IF:
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
    return;
  }

  /**
   * <logicalExpr> ::= "(" <logicalExpr> ")"
   * | "!" <logicalExpr>
   * | <logicalExpr> ("&&" | "||") <logicalExpr>
   * | <conditionalExpr>
   * 
   * @param st
   * @throws SyntaxError
   */
  private void logicalExpr(SyntaxTree st) throws SyntaxError {
    switch (currentToken().kind) {
      case OPEN_BRACKET:
        matchToken(TokenType.OPEN_BRACKET, st);
        logicalExpr(st.insertSubtree(new Token(TokenType.LOGICAL_EXPR,"")));
        break;

      case NOT:
        matchToken(TokenType.NOT, st);
        logicalExpr(st.insertSubtree(new Token(TokenType.LOGICAL_EXPR,"")));
        break;
      case LAND:
        matchToken(TokenType.LAND, st);
      case LOR:
        matchToken(TokenType.LOR, st);
      default:
        break;
    }
  }

  /**
   * <arithmeticExpr> ::= <term> (( "+" | "-") <arithmeticExpr>)*
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void arithmeticExpr(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    term(st.insertSubtree(new Token(TokenType.TERM, "")));
    if (checkToken(TokenType.PLUS)) {
      matchToken(TokenType.PLUS, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkToken(TokenType.MINUS)) {
      matchToken(TokenType.MINUS, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    }
  }

  /**
   * <term> ::= <unary> (("*" | "/") <term>)*
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void term(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    unary(st.insertSubtree(new Token(TokenType.UNARY, "")));
    if (checkToken(TokenType.ASTERISK)) {
      matchToken(TokenType.ASTERISK, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkToken(TokenType.SLASH)) {
      matchToken(TokenType.SLASH, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    }
  }

  /**
   * <unary>::= "(" <arithmeticExpr> ")"
   * | ("+" | "-")? <primary>
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void unary(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    switch (currentToken().kind) {
      case OPEN_BRACKET:
        matchToken(TokenType.OPEN_BRACKET, st);
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        matchToken(TokenType.CLOSE_BRACKET, st);
        break;

      case PLUS:
        matchToken(TokenType.PLUS, st);
        primary(st.insertSubtree(new Token(TokenType.PRIMARY, "")));
        break;

      case MINUS:
        matchToken(TokenType.MINUS, st);
        primary(st.insertSubtree(new Token(TokenType.PRIMARY, "")));
        break;

      default:
        primary(st.insertSubtree(new Token(TokenType.PRIMARY, "")));
        break;
    }
  }

  /**
   * {@summary} <primary> ::= <vbool> | <vint> | <ident>
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void primary(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    switch (currentToken().kind) {
      case V_INT:
        matchToken(TokenType.V_INT, st);
        break;
      /*
       * V_BOOL now bypasses primary
       * case V_BOOL:
       * st.insertSubtree(currentToken());
       * nextToken();
       * break;
       */
      case IDENT:
        if (!declaredIdentifier.contains(currentToken().lexem)) {
          throw new UnknownIdentifierError(currentToken());
        }
        matchToken(TokenType.IDENT, st);
        // st.insertSubtree(currentToken());
        break;

      default:
        throw new SyntaxError(currentToken());
    }
  }

}
