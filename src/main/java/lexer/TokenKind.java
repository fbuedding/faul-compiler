package lexer;

/**
 * Tokens that can be used
 */
public enum TokenKind {
  /**
   * End of input
   */
  EOF,
  /**
   * Statment terminator ';'
   */
  SEMICOLON,
  /**
   * Integer number (value)
   */
  V_INT,
  /**
   * Boolean (value)
   */
  V_BOOL,
  /**
   * Opening of an block statemenit
   */
  OPEN_PARANTHESES,
  /**
   * Closing of an block statement
   */
  CLOSE_PARANTHESES,
  /**
   * Indetifier, varaible name
   */
  IDENT,
  // Keywords
  /**
   * int keyword
   */
  INT,
  /**
   * bool keyword
   */
  BOOL,
  /**
   * if keyword
   */
  IF,
  /**
   * else keyword
   */
  ELSE,
  /**
   * while keyword
   */
  WHILE,
  // Operators
  /**
   * =
   */
  EQ,
  /**
   * +
   */
  PLUS,
  /**
   * -
   */
  MINUS,
  /**
   * *
   */
  ASTERISK,
  /**
   * /
   */
  SLASH,
  /**
   * %
   */
  PERCENT,
  /**
   * (
   */
  OPEN_BRACKET,
  /**
   * )
   */
  CLOSE_BRACKET,
  /**
   * ==
   */
  EQEQ,
  /**
   * !=
   */
  NOTEQ,
  /**
   * >
   */
  GT,
  /**
   * >=
   */
  GTEQ,
  /**
   * <
   */
  LT,
  /**
   * <=
   */
  LTEQ,
  /**
   * !
   */
  NOT,
  /**
   * Bitwise or
   */
  OR,
  /**
   * Bitwise and
   */
  AND,
  /**
   * logical OR
   */
  LOR,
  /**
   * logical AND
   */
  LAND,
  // ######## Token types for AST
  PROGRAM,
  STATEMENT,
  EXPRESSION,
  EQUALITY,
  COMPARISON,
  ARITHMETIC_EXPR,
  TERM,
  UNARY,
  PRIMARY
}
