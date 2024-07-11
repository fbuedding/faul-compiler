package ast;

public enum AstNodeKinds {
  PROGRAM,
  DECLARATION,
  ADDRESS,
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
   * - but unary
   */
  NEG,
  /**
   * function call operator
   */
  FUNC_CALL,
  /**
   * -
   */
  MINUS,
  /**
   * *
   */
  MUL,
  /**
   * /
   */
  DIV,
  /**
   * /
   */
  MOD,
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
