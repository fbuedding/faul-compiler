package parser;

public  final class Constants {

  private Constants() {
  };

  // Statements
  public static final int STATEMENT_TYPE = 0;
  // If
  public static final int IF_EXPRESSION = 2;
  public static final int IF_STATEMENT = 5;
  // Declarations
  public static final int DECLARATION_TYPE = 0;
  public static final int DECLARERATION_IDENTIFIER = 1;
  public static final int DECLARERATION_EXPRESSION = 3;
  // Assignments
  public static final int ASSIGNMENT_IDENTIFIER = 0;
  public static final int ASSIGNMENT_EXPRESSION = 2;
  // The Operator of an expression, equality, comparision, arithmeticExpr or term is always at the same place
  public static final int FIRST_OPERAND = 0;
  public static final int OPERATOR = 1;
  public static final int SECOND_OPERAND = 2;
  // Unary  child indeces
  public static final int UNARY_OPERATOR = 0;
  public static final int UNARY = 1;
  // Primary  child indeces
  public static final int PRIMARY_VALUE = 0;
  public static final int PRIMARY_EXPRESSION = 1;
}
