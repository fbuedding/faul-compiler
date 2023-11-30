package parser;

import java.util.Stack;
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
  private Stack<Integer> visibilityStack = new Stack<Integer>();

  private Vector<String> declaredIdentifier = new Vector<String>();

  public Parser(Token[] tokens) {
    this.tokens = tokens;
    if (tokens.length < 2)
      throw new Error("Empty File!");
    visibilityStack.push(0);
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
      return tokens[tokens.length - 1]; // EOF
    }
    return tokens[nextToken];
  }

  // ################# Begin parsing methods #################

  /**
   * {@summary}<program>::= <statement>*
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
   * ```
   * <statement> ::= "int" <ident> "=" <expression> <semi>
   * | "bool" <ident> "=" <expression> <semi>
   * | "if" "(" <expression> ")" "{" <statement>* "}"
   * | <ident> "=" <expression> <semi>
   * ```
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
        matchToken(TokenType.INT, st);
        String ident = currentToken().lexem;
        if (declaredIdentifier.contains(ident)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenType.IDENT, st);
        matchToken(TokenType.EQ, st);
        expression(st.insertSubtree(new Token(TokenType.EXPRESSION, "")));
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
        expression(st.insertSubtree(new Token(TokenType.EXPRESSION, "")));
        matchToken(TokenType.SEMICOLON, st);
        declaredIdentifier.add(boolIdent);
        break;
      case IF:
        matchToken(TokenType.IF, st);
        matchToken(TokenType.OPEN_BRACKET, st);
        expression(st.insertSubtree(new Token(TokenType.EXPRESSION, "")));
        matchToken(TokenType.CLOSE_BRACKET, st);
        matchToken(TokenType.OPEN_PARANTHESES, st);
        visibilityStack.push(declaredIdentifier.size());
        while (!checkToken(TokenType.CLOSE_PARANTHESES)) {
          statement(st.insertSubtree(new Token(TokenType.STATEMENT, "")));
        }
        matchToken(TokenType.CLOSE_PARANTHESES, st);
        int elementsToBeRemoved = declaredIdentifier.size() - visibilityStack.pop();
        for (int i = 0; i < elementsToBeRemoved; i++) {
          declaredIdentifier.removeLast();
        }
        break;
      case IDENT:
        if (!declaredIdentifier.contains(currentToken().lexem)) {
          throw new UnknownIdentifierError(currentToken());
        }
        matchToken(TokenType.IDENT, st);
        matchToken(TokenType.EQ, st);
        expression(st.insertSubtree(new Token(TokenType.EXPRESSION, "")));
        matchToken(TokenType.SEMICOLON, st);
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
    return;
  }

  /**
   * ```
   * <expression> ::= <equality>
   * ```
   *
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  public void expression(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    equality(st.insertSubtree(new Token(TokenType.EQUALITY, "")));
    if (checkToken(TokenType.LAND)) {
     matchToken(TokenType.LAND, st); 
     expression(st);
    } else if (checkToken(TokenType.LOR)) {
     matchToken(TokenType.LOR, st); 
     expression(st);
    } else if (checkToken(TokenType.AND)) {
     matchToken(TokenType.AND, st); 
     expression(st);
    } else if (checkToken(TokenType.OR)) {
     matchToken(TokenType.OR, st); 
     expression(st);
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
  public void equality(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    comparison(st.insertSubtree(new Token(TokenType.COMPARISON, "")));
    if (checkToken(TokenType.NOTEQ)) {
      matchToken(TokenType.NOTEQ, st);
      equality(st.insertSubtree(new Token(TokenType.EQUALITY, "")));
    } else if (checkToken(TokenType.EQEQ)) {
      matchToken(TokenType.EQEQ, st);
      equality(st.insertSubtree(new Token(TokenType.EQUALITY, "")));
    }
  }

  /**
   * ```
   * <comparision> ::= <arithmeticExpr> ( (">" | ">=" | "<" | "<=")
   * <comparision>)*
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void comparison(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    arithmeticExpr(st.insertSubtree(new Token(TokenType.ARITHMETIC_EXPR, "")));
    if (checkToken(TokenType.GT)) {
      matchToken(TokenType.GT, st);
      comparison(st.insertSubtree(new Token(TokenType.COMPARISON, "")));
    } else if (checkToken(TokenType.GTEQ)) {
      matchToken(TokenType.GTEQ, st);
      comparison(st.insertSubtree(new Token(TokenType.COMPARISON, "")));
    } else if (checkToken(TokenType.LT)) {
      matchToken(TokenType.LT, st);
      comparison(st.insertSubtree(new Token(TokenType.COMPARISON, "")));
    } else if (checkToken(TokenType.LTEQ)) {
      matchToken(TokenType.LTEQ, st);
      comparison(st.insertSubtree(new Token(TokenType.COMPARISON, "")));
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
   * ```
   * <term> ::= <unary> (("*" | "/") <term>)*
   * ```
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void term(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    unary(st.insertSubtree(new Token(TokenType.UNARY, "")));
    if (checkToken(TokenType.ASTERISK)) {
      matchToken(TokenType.ASTERISK, st);
      term(st.insertSubtree(new Token(TokenType.TERM, "")));
    } else if (checkToken(TokenType.SLASH)) {
      matchToken(TokenType.SLASH, st);
      term(st.insertSubtree(new Token(TokenType.TERM, "")));
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
  private void unary(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    if (checkToken(TokenType.NOT)) {
      matchToken(TokenType.NOT, st);
      unary(st.insertSubtree(new Token(TokenType.UNARY, "")));
    } else if (checkToken(TokenType.PLUS)) {
      matchToken(TokenType.PLUS, st);
      unary(st.insertSubtree(new Token(TokenType.UNARY, "")));
    } else {
      primary(st.insertSubtree(new Token(TokenType.PRIMARY, "")));
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
  private void primary(SyntaxTree st) throws SyntaxError, UnknownIdentifierError {
    TokenType[] expected = new TokenType[] { TokenType.V_INT, TokenType.V_BOOL, TokenType.OPEN_BRACKET,
        TokenType.IDENT };
    if (checkToken(TokenType.OPEN_BRACKET)) {
      matchToken(TokenType.OPEN_BRACKET, st);
      expression(st.insertSubtree(new Token(TokenType.EXPRESSION)));
      matchToken(TokenType.CLOSE_BRACKET, st);
    } else if (checkToken(TokenType.V_BOOL)) {
      matchToken(TokenType.V_BOOL, st);
    } else if (checkToken(TokenType.V_INT)) {
      matchToken(TokenType.V_INT, st);
    } else if (checkToken(TokenType.IDENT)) {
      if (!declaredIdentifier.contains(currentToken().lexem)) {
        throw new UnknownIdentifierError(currentToken());
      }
      matchToken(TokenType.IDENT, st);
    } else {
      throw new SyntaxError(currentToken(), expected);
    }
  }

}
