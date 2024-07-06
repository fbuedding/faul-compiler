package ast;

public enum AstNodeKinds {
  PROGRAM,
  DECLARATION,
  ADRESS,
  ASSIGNMENT,
  WHILE,
  IF,
  CONDITION,
  BRANCH,
  INTEGER,
  BOOLEAN,
  /**
   * Indetifier, varaible name
   */
  IDENT,
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
}
