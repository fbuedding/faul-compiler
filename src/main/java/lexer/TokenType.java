package lexer;

/**
 * Tokens that can be used
 */
public enum TokenType {
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
  NOT
}
