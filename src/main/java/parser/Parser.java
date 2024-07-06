package parser;

import java.util.Stack;
import java.util.Vector;

import lexer.Token;
import lexer.TokenKind;

/**
 * 
 */
public class Parser {
  private Token[] tokens;
  private int currentToken = 0;
  private int nextToken = 1;
  private Stack<Integer> scopes = new Stack<Integer>();

  private Vector<String> symbolTable = new Vector<String>();

  public Parser(Token[] tokens) {
    this.tokens = tokens;
    if (tokens.length < 2)
      throw new Error("Empty File!");
    scopes.push(0);
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
  public void program(ParseTree st) throws SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError {

    while (!checkToken(TokenKind.EOF)) {
      this.statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "")));
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
  private void statement(ParseTree st) throws SyntaxError, UnknownIdentifierError, IndentifierAlreadyDeclaredError {
    TokenKind[] expected = new TokenKind[] { TokenKind.INT, TokenKind.BOOL, TokenKind.IF, TokenKind.IDENT };
    switch (currentToken().kind) {
      case INT:
        matchToken(TokenKind.INT, st);
        String ident = currentToken().lexem;
        if (symbolTable.contains(ident)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenKind.IDENT, st);
        matchToken(TokenKind.EQ, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
        matchToken(TokenKind.SEMICOLON, st);
        symbolTable.add(ident);
        break;
      case BOOL:
        matchToken(TokenKind.BOOL, st);
        String boolIdent = currentToken().lexem;
        if (symbolTable.contains(boolIdent)) {
          throw new IndentifierAlreadyDeclaredError(currentToken());
        }
        matchToken(TokenKind.IDENT, st);
        matchToken(TokenKind.EQ, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
        matchToken(TokenKind.SEMICOLON, st);
        symbolTable.add(boolIdent);
        break;
      case IF: {
        matchToken(TokenKind.IF, st);
        matchToken(TokenKind.OPEN_BRACKET, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
        matchToken(TokenKind.CLOSE_BRACKET, st);
        matchToken(TokenKind.OPEN_PARANTHESES, st);
        scopes.push(symbolTable.size());
        while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
          statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "")));
        }
        matchToken(TokenKind.CLOSE_PARANTHESES, st);
        int elementsToBeRemoved = symbolTable.size() - scopes.pop();
        for (int i = 0; i < elementsToBeRemoved; i++) {
          symbolTable.removeLast();
        }
        // Since I want the else branch to be under the if statement
        if (checkToken(TokenKind.ELSE)) {
          matchToken(TokenKind.ELSE, st);
          matchToken(TokenKind.OPEN_PARANTHESES, st);
          scopes.push(symbolTable.size());
          while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
            statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "")));
          }
          matchToken(TokenKind.CLOSE_PARANTHESES, st);
          elementsToBeRemoved = symbolTable.size() - scopes.pop();
          for (int i = 0; i < elementsToBeRemoved; i++) {
            symbolTable.removeLast();
          }

        }
        break;
      }
      case WHILE: {
        matchToken(TokenKind.WHILE, st);
        matchToken(TokenKind.OPEN_BRACKET, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
        matchToken(TokenKind.CLOSE_BRACKET, st);
        matchToken(TokenKind.OPEN_PARANTHESES, st);
        scopes.push(symbolTable.size());
        while (!checkToken(TokenKind.CLOSE_PARANTHESES)) {
          statement(st.insertSubtree(new Token(TokenKind.STATEMENT, "")));
        }
        matchToken(TokenKind.CLOSE_PARANTHESES, st);
        int elementsToBeRemoved = symbolTable.size() - scopes.pop();
        for (int i = 0; i < elementsToBeRemoved; i++) {
          symbolTable.removeLast();
        }
        break;
      }
      case IDENT:
        if (!symbolTable.contains(currentToken().lexem)) {
          throw new UnknownIdentifierError(currentToken());
        }
        matchToken(TokenKind.IDENT, st);
        matchToken(TokenKind.EQ, st);
        expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
        matchToken(TokenKind.SEMICOLON, st);
        break;
      default:
        throw new SyntaxError(currentToken(), expected);
    }
    return;
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
  public void expression(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "")));
    if (checkToken(TokenKind.LAND)) {
      matchToken(TokenKind.LAND, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
    } else if (checkToken(TokenKind.LOR)) {
      matchToken(TokenKind.LOR, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
    } else if (checkToken(TokenKind.AND)) {
      matchToken(TokenKind.AND, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
    } else if (checkToken(TokenKind.OR)) {
      matchToken(TokenKind.OR, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION, "")));
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
  public void equality(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "")));
    if (checkToken(TokenKind.NOTEQ)) {
      matchToken(TokenKind.NOTEQ, st);
      equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "")));
    } else if (checkToken(TokenKind.EQEQ)) {
      matchToken(TokenKind.EQEQ, st);
      equality(st.insertSubtree(new Token(TokenKind.EQUALITY, "")));
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
  private void comparison(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    arithmeticExpr(st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "")));
    if (checkToken(TokenKind.GT)) {
      matchToken(TokenKind.GT, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "")));
    } else if (checkToken(TokenKind.GTEQ)) {
      matchToken(TokenKind.GTEQ, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "")));
    } else if (checkToken(TokenKind.LT)) {
      matchToken(TokenKind.LT, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "")));
    } else if (checkToken(TokenKind.LTEQ)) {
      matchToken(TokenKind.LTEQ, st);
      comparison(st.insertSubtree(new Token(TokenKind.COMPARISON, "")));
    }
  }

  /**
   * <arithmeticExpr> ::= <term> (( "+" | "-") <arithmeticExpr>)?
   * 
   * @param st
   * @throws SyntaxError
   * @throws UnknownIdentifierError
   */
  private void arithmeticExpr(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    term(st.insertSubtree(new Token(TokenKind.TERM, "")));
    if (checkToken(TokenKind.PLUS)) {
      matchToken(TokenKind.PLUS, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "")));
    } else if (checkToken(TokenKind.MINUS)) {
      matchToken(TokenKind.MINUS, st);
      arithmeticExpr(st.insertSubtree(new Token(TokenKind.ARITHMETIC_EXPR, "")));
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
  private void term(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    unary(st.insertSubtree(new Token(TokenKind.UNARY, "")));
    if (checkToken(TokenKind.ASTERISK)) {
      matchToken(TokenKind.ASTERISK, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "")));
    } else if (checkToken(TokenKind.SLASH)) {
      matchToken(TokenKind.SLASH, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "")));
    }else if (checkToken(TokenKind.PERCENT)) {
      matchToken(TokenKind.PERCENT, st);
      term(st.insertSubtree(new Token(TokenKind.TERM, "")));
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
  private void unary(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    if (checkToken(TokenKind.NOT)) {
      matchToken(TokenKind.NOT, st);
      unary(st.insertSubtree(new Token(TokenKind.UNARY, "")));
    } else if (checkToken(TokenKind.MINUS)) {
      matchToken(TokenKind.MINUS, st);
      unary(st.insertSubtree(new Token(TokenKind.UNARY, "")));
    } else {
      primary(st.insertSubtree(new Token(TokenKind.PRIMARY, "")));
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
  private void primary(ParseTree st) throws SyntaxError, UnknownIdentifierError {
    TokenKind[] expected = new TokenKind[] { TokenKind.V_INT, TokenKind.V_BOOL, TokenKind.OPEN_BRACKET,
        TokenKind.IDENT };
    if (checkToken(TokenKind.OPEN_BRACKET)) {
      matchToken(TokenKind.OPEN_BRACKET, st);
      expression(st.insertSubtree(new Token(TokenKind.EXPRESSION)));
      matchToken(TokenKind.CLOSE_BRACKET, st);
    } else if (checkToken(TokenKind.V_BOOL)) {
      matchToken(TokenKind.V_BOOL, st);
    } else if (checkToken(TokenKind.V_INT)) {
      matchToken(TokenKind.V_INT, st);
    } else if (checkToken(TokenKind.IDENT)) {
      if (!symbolTable.contains(currentToken().lexem)) {
        throw new UnknownIdentifierError(currentToken());
      }
      matchToken(TokenKind.IDENT, st);
    } else {
      throw new SyntaxError(currentToken(), expected);
    }
  }

}
