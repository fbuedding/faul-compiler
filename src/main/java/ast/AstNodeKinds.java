package ast;

public enum AstNodeKinds {
  PROGRAM,
  DECLARATION,
  ADRESS,
  ASSIGNMENT,
  IF,
  CONDITION,
  BRANCH,
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
