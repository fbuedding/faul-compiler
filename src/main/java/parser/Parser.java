package parser;

import lexer.Token;
import lexer.TokenKind;

/**
 * 
 */
public class Parser {
  private final Token[] tokens;
  private int currentToken = 0;
  private int nextToken = 1;

  public Parser(Token[] tokens) {
    this.tokens = tokens;
    if (tokens.length < 2)
      throw new Error("Empty File!");
  }

  /**
   * Good for checking if current token is a specific token without actually
   * matching it
   * 
   * @param tt
   * @return true if current token is of kind
   */
  private boolean checkToken(TokenKind tt) {
    return tokens[currentToken].kind == tt;
  }

  /**
   * Matches token but doesn't insert it in the ParseTree
   * 
   * @param tt
   * @throws SyntaxError
   */
  private void matchToken(TokenKind tt) throws SyntaxError {
    if (!checkToken(tt)) {
      throw new SyntaxError(currentToken(), tt);
    }
    nextToken();
  }

  /**
   * Matches {@link Token} and inserts in {@link ParseTree}
   * 
   * @param tt
   * @param st
   * @throws SyntaxError
   */
  private void matchToken(TokenKind tt, ParseTree st) throws SyntaxError {
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

  // ################# Begin parsing methods #################

  /**
   * {@summary}<program>::= <statement>*
   * 
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   * @throws IndentifierAlreadyDeclaredError*
   */
  public void program(ParseTree st) throws SyntaxError {

    while (!checkToken(TokenKind.EOF)) {
      this.statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * ```
   * <statement> ::= "int" <ident> "=" <expression> <semi>
   * | "bool" <ident> "=" <expression> <semi>
   * | "if" "(" <expression> ")" "{" <statement>* "}"
   * | <ident> "=" <expression> <semi>
   * | <ident> <function_call> <semi>
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   * @throws IndentifierAlreadyDeclaredError
   */
  private void statement(ParseTree st) throws SyntaxError {
    TokenKind[] expected = new TokenKind[] { TokenKind.INT, TokenKind.BOOL, TokenKind.IF, TokenKind.IDENT };
    switch (currentToken().kind) {
      case INT:
        matchToken(TokenKind.INT, st);
        matchToken(TokenKind.IDENT, st);
        matchToken(TokenKind.EQ, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
        matchToken(TokenKind.SEMICOLON, st);
        break;
      case BOOL:
        matchToken(TokenKind.BOOL, st);
        matchToken(TokenKind.IDENT, st);
        matchToken(TokenKind.EQ, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
        matchToken(TokenKind.SEMICOLON, st);
        break;
      case IF: {
        matchToken(TokenKind.IF, st);
        matchToken(TokenKind.OPEN_BRACKET, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
        matchToken(TokenKind.CLOSE_BRACKET, st);
        matchToken(TokenKind.OPEN_PARANTHESES, st);
        while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
          statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "", currentToken().line, currentToken().linePos)));
        }
        matchToken(TokenKind.CLOSE_PARANTHESES, st);
        // Since I want the else branch to be under the if statement
        if (checkToken(TokenKind.ELSE)) {
          matchToken(TokenKind.ELSE, st);
          matchToken(TokenKind.OPEN_PARANTHESES, st);
          while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
            statement(
                st.insertSubtree(new Token(TokenKind.STATEMENT, "", currentToken().line, currentToken().linePos)));
          }
          matchToken(TokenKind.CLOSE_PARANTHESES, st);

        }
        break;
      }
      case WHILE: {
        matchToken(TokenKind.WHILE, st);
        matchToken(TokenKind.OPEN_BRACKET, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
        matchToken(TokenKind.CLOSE_BRACKET, st);
        matchToken(TokenKind.OPEN_PARANTHESES, st);
        while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
          statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "", currentToken().line, currentToken().linePos)));
        }
        matchToken(TokenKind.CLOSE_PARANTHESES, st);
        break;
      }
      case IDENT:
        ident(st);
        if (checkToken(TokenKind.EQ)) {
          matchToken(TokenKind.EQ, st);
          expression(
              st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
        }
        matchToken(TokenKind.SEMICOLON, st);
        break;
      case SEMICOLON:
        matchToken(TokenKind.SEMICOLON);
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
  }

  /**
   * ```
   * <expression> ::= <equality> ( ("&&" | "||" | "&" | "|") <expression>)?
   * ```
   *
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void expression(ParseTree st) throws SyntaxError {
    equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "", currentToken().line, currentToken().linePos)));
    if (checkToken(TokenKind.LAND)) {
      matchToken(TokenKind.LAND, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.LOR)) {
      matchToken(TokenKind.LOR, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.AND)) {
      matchToken(TokenKind.AND, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.OR)) {
      matchToken(TokenKind.OR, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * ```
   * <equality> ::= <comparision> (("!=" | "==") <equality>)?
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void equality(ParseTree st) throws SyntaxError {
    comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "", currentToken().line, currentToken().linePos)));
    if (checkToken(TokenKind.NOTEQ)) {
      matchToken(TokenKind.NOTEQ, st);
      equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.EQEQ)) {
      matchToken(TokenKind.EQEQ, st);
      equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * ```
   * <comparision> ::= <arithmeticExpr> ( (">" | ">=" | "<" | "<=")
   * <comparision>)?
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void comparison(ParseTree st) throws SyntaxError {
    arithmeticExpr(
        st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "", currentToken().line, currentToken().linePos)));
    if (checkToken(TokenKind.GT)) {
      matchToken(TokenKind.GT, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.GTEQ)) {
      matchToken(TokenKind.GTEQ, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.LT)) {
      matchToken(TokenKind.LT, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.LTEQ)) {
      matchToken(TokenKind.LTEQ, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * <arithmeticExpr> ::= <term> (( "+" | "-") <arithmeticExpr>)?
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void arithmeticExpr(ParseTree st) throws SyntaxError {
    term(st.insertSubtree(new Token(TokenKind.TERM, "", currentToken().line, currentToken().linePos)));
    if (checkToken(TokenKind.PLUS)) {
      matchToken(TokenKind.PLUS, st);
      arithmeticExpr(
          st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.MINUS)) {
      matchToken(TokenKind.MINUS, st);
      arithmeticExpr(
          st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * ```
   * <term> ::= <unary> (("*" | "/") <term>)?
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void term(ParseTree st) throws SyntaxError {
    unary(st.insertSubtree(new Token(TokenKind.UNARY, "", currentToken().line, currentToken().linePos)));
    if (checkToken(TokenKind.ASTERISK)) {
      matchToken(TokenKind.ASTERISK, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.SLASH)) {
      matchToken(TokenKind.SLASH, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.PERCENT)) {
      matchToken(TokenKind.PERCENT, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "", currentToken().line, currentToken().linePos)));
    }

  }

  /**
   * ```
   * <unary> ::= ("!" | "-") <unary>
   * | <primary>
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void unary(ParseTree st) throws SyntaxError {
    if (checkToken(TokenKind.NOT)) {
      matchToken(TokenKind.NOT, st);
      unary(st.insertSubtree(new Token(TokenKind.UNARY, "", currentToken().line, currentToken().linePos)));
    } else if (checkToken(TokenKind.MINUS)) {
      matchToken(TokenKind.MINUS, st);
      unary(st.insertSubtree(new Token(TokenKind.UNARY, "", currentToken().line, currentToken().linePos)));
    } else {
      primary(st.insertSubtree(new Token(TokenKind.PRIMARY, "", currentToken().line, currentToken().linePos)));
    }
  }

  /**
   * ```
   * <primary> ::= <vbool> | <vint> | <ident>
   * | "(" <expression> ")"
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void primary(ParseTree st) throws SyntaxError {
    TokenKind[] expected = new TokenKind[] { TokenKind.V_INT, TokenKind.V_BOOL, TokenKind.OPEN_BRACKET,
        TokenKind.IDENT };
    if (checkToken(TokenKind.OPEN_BRACKET)) {
      matchToken(TokenKind.OPEN_BRACKET, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, currentToken().line, currentToken().linePos)));
      matchToken(TokenKind.CLOSE_BRACKET, st);
    } else if (checkToken(TokenKind.V_BOOL)) {
      matchToken(TokenKind.V_BOOL, st);
    } else if (checkToken(TokenKind.V_INT)) {
      matchToken(TokenKind.V_INT, st);
    } else if (checkToken(TokenKind.IDENT)) {
      ident(st);
    } else {
      throw new SyntaxError(currentToken(), expected);
    }
  }

  /**
   * <ident> ::= ("_" | [a-z]) ("_" | [a-z] | [0-9])* (<function_call>)?
   * 
   * @param st
   * @throws SyntaxError
   */
  private void ident(ParseTree st) throws SyntaxError {
    matchToken(TokenKind.IDENT, st);
    if (checkToken(TokenKind.OPEN_BRACKET)) {
      functionCall(st.insertSubtree(new Token(currentToken(), TokenKind.FUNC_CALL)));
    }
  }

  /**
   * <function_call> ::= "(" (<expression> ("," <expression>)*)? ")"
   * 
   * @param st
   * @throws SyntaxError
   */
  private void functionCall(ParseTree st) throws SyntaxError {
    matchToken(TokenKind.OPEN_BRACKET);
    // no args
    if (checkToken(TokenKind.CLOSE_BRACKET)) {
      matchToken(TokenKind.CLOSE_BRACKET);
      return;
    }

    expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, currentToken().line, currentToken().linePos)));
    while (checkToken(TokenKind.COMMA)) {
      matchToken(TokenKind.COMMA);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, currentToken().line, currentToken().linePos)));
    }
    matchToken(TokenKind.CLOSE_BRACKET);
  }

}
