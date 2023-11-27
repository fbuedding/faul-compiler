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

  private boolean checkPeekToken(TokenType tt) {
    return tokens[nextToken].kind == tt;
  }

  private void matchToken(TokenType tt) throws SyntaxError {
    if (!checkToken(tt)) {
      throw new SyntaxError(currentToken(), tt);
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
   * @throws IndentifierAlreadyDeclaredError
   */
  private void statement(SyntaxTree st) throws SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError {
    TokenType[] expected = new TokenType[] { TokenType.INT, TokenType.BOOL, TokenType.IF, TokenType.IDENT };
    switch (currentToken().kind) {
      case INT:
        st.insertSubtree(currentToken());
        nextToken();
        st.insertSubtree(currentToken());
        String ident = currentToken().lexem;
        if (declaredIdentifier.contains(ident)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenType.IDENT);
        st.insertSubtree(currentToken());
        matchToken(TokenType.EQ);
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        st.insertSubtree(currentToken()); 
        matchToken(TokenType.SEMICOLON);
        declaredIdentifier.add(ident);
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
      st.insertSubtree(currentToken());
      matchToken(TokenType.PLUS);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkToken(TokenType.MINUS)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.MINUS);
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
      st.insertSubtree(currentToken());
      matchToken(TokenType.ASTERISK);
      arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    } else if (checkToken(TokenType.SLASH)) {
      st.insertSubtree(currentToken());
      matchToken(TokenType.SLASH);
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
        st.insertSubtree(currentToken());
        matchToken(TokenType.OPEN_BRACKET);
        arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
        st.insertSubtree(currentToken());
        matchToken(TokenType.CLOSE_BRACKET);
        break;

      case PLUS:
        st.insertSubtree(currentToken());
        matchToken(TokenType.PLUS);
        primary(st.insertSubtree(new Token(TokenType.PRIMARY, "")));
        break;

      case MINUS:
        st.insertSubtree(currentToken());
        matchToken(TokenType.MINUS);
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
        st.insertSubtree(currentToken());
        nextToken();
        break;
      case V_BOOL:
        st.insertSubtree(currentToken());
        nextToken();
        break;
      case IDENT:
        if (!declaredIdentifier.contains(currentToken().lexem)) {
          throw new UnknownIdentifierError(currentToken());
        }
        st.insertSubtree(currentToken());
        break;

      default:
        throw new SyntaxError(currentToken());
    }
  }

}
