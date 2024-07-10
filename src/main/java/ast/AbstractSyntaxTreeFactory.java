package ast;

import static parser.Constants.ASSIGNMENT_EXPRESSION;
import static parser.Constants.ASSIGNMENT_IDENTIFIER;
import static parser.Constants.DECLARATION_TYPE;
import static parser.Constants.DECLARERATION_EXPRESSION;
import static parser.Constants.DECLARERATION_IDENTIFIER;
import static parser.Constants.EMPTY_BLOCK_SIZE;
import static parser.Constants.FIRST_OPERAND;
import static parser.Constants.IF_EXPRESSION;
import static parser.Constants.IF_STATEMENT;
import static parser.Constants.OPERATOR;
import static parser.Constants.PRIMARY_EXPRESSION;
import static parser.Constants.PRIMARY_VALUE;
import static parser.Constants.SECOND_OPERAND;
import static parser.Constants.STATEMENT_TYPE;
import static parser.Constants.UNARY;
import static parser.Constants.UNARY_OPERATOR;
import static parser.Constants.WHILE_EXPRESSION;

import error.CompileError;
import lexer.Token;
import lexer.TokenKind;
import parser.ParseTree;

public class AbstractSyntaxTreeFactory {
  public AbstractSyntaxTree ast;
  public SymbolTable symbolTable;

  public AbstractSyntaxTreeFactory() {
    ast = new AbstractSyntaxTree(AstNodeKinds.PROGRAM, Types.NONE, Types.NONE, 0, 0);
    symbolTable = new SymbolTable();
  }

  public AbstractSyntaxTree fromParseTree(ParseTree st) throws CompileError {
    processProgram(st, ast, symbolTable);
    ast.checkTypes();
    return ast;
  }

  /**
   * Handle a declaration, this isn't a non terminal of the grammar, but is
   * implicit
   * 
   * @param ast
   * @param pt
   * @param sTable
   */
  void declaration(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    Token t = pt.getChild(DECLARERATION_IDENTIFIER).token;
    String ident = t.lexem;
    if (sTable.has(ident)) {
      throw new IndentifierAlreadyDeclaredError(ident, t.line, t.linePos);
    }
    Types type;
    switch (pt.getChild(DECLARATION_TYPE).token.lexem) {
      case "int":
        type = Types.INTEGER;
        break;
      case "bool":
        type = Types.BOOLEAN;
        break;

      default:
        // TODO: Meaningfull Error
        throw new Error("Every declaration should have a type");
    }
    int adress = sTable.insert(ident, type);
    ast.insertSubTree(AstNodeKinds.IDENT, type, Types.NONE, t.line, t.linePos, ident);
    ast.insertSubTree(AstNodeKinds.ADDRESS, Types.MEMORY_ADDRESS, Types.NONE, t.line, t.linePos,
        "" + adress);
  }

  /**
   * Handle an assignment, this isn't a non terminal of the grammar, but is
   * implicit
   * 
   * @param ast
   * @param pt
   * @param sTable
   * @throws CompileError
   */
  void assignment(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    ParseTree ident = pt.getChild(ASSIGNMENT_IDENTIFIER);
    Symbol s = sTable.get(ident.token.lexem);
    if (s == null) {
      throw new UnknownIdentifierError(ident.token.lexem, ident.token.line, ident.token.linePos);
    }
    ast.type = s.getType();
    // insert ident child
    ast.insertSubTree(AstNodeKinds.IDENT, s.getType(), s.getType(), ident.token.line, ident.token.linePos,
        ident.token.lexem);
    // handle expression
    expression(ast, pt.getChild(ASSIGNMENT_EXPRESSION), sTable);
  }

  void expression(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {

    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }

    if (pt.getChildCount() == 1) {
      equality(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (pt.getChild(OPERATOR).getKind()) {
        case LAND:
          tmp = ast.insertSubTree(AstNodeKinds.LAND, Types.BOOLEAN, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;

        case LOR:
          tmp = ast.insertSubTree(AstNodeKinds.LOR, Types.BOOLEAN, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;

        case OR:
          tmp = ast.insertSubTree(AstNodeKinds.OR, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;

        case AND:
          tmp = ast.insertSubTree(AstNodeKinds.AND, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);
          break;

        default:
          // TODO: Create real Error
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      equality(tmp, pt.getChild(FIRST_OPERAND), sTable);
      expression(tmp, pt.getChild(SECOND_OPERAND), sTable);
    }
  }

  void equality(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      comparision(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (op.getKind()) {
        case NOTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.NOTEQ, Types.UNKNOWN, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;
        case EQEQ:
          tmp = ast.insertSubTree(AstNodeKinds.EQEQ, Types.UNKNOWN, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      comparision(tmp, pt.getChild(FIRST_OPERAND), sTable);
      equality(tmp, pt.getChild(SECOND_OPERAND), sTable);
    }

  }

  void comparision(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      arithmeticExpr(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (op.getKind()) {
        case LT:
          tmp = ast.insertSubTree(AstNodeKinds.LT, Types.INTEGER, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;
        case LTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.LTEQ, Types.INTEGER, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;
        case GT:
          tmp = ast.insertSubTree(AstNodeKinds.GT, Types.INTEGER, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;
        case GTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.GTEQ, Types.INTEGER, Types.BOOLEAN, op.token.line,
              op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      arithmeticExpr(tmp, pt.getChild(FIRST_OPERAND), sTable);
      comparision(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void arithmeticExpr(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      term(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (op.getKind()) {
        case PLUS:
          tmp = ast.insertSubTree(AstNodeKinds.PLUS, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;
        case MINUS:
          tmp = ast.insertSubTree(AstNodeKinds.MINUS, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      term(tmp, pt.getChild(FIRST_OPERAND), sTable);
      arithmeticExpr(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void term(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      unary(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (op.getKind()) {
        case ASTERISK:
          tmp = ast.insertSubTree(AstNodeKinds.MUL, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;
        case SLASH:
          tmp = ast.insertSubTree(AstNodeKinds.DIV, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;
        case PERCENT:
          tmp = ast.insertSubTree(AstNodeKinds.MOD, Types.INTEGER, Types.INTEGER, op.token.line,
              op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      unary(tmp, pt.getChild(FIRST_OPERAND), sTable);
      term(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void unary(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (pt.getChildCount() != 1 && pt.getChildCount() != 2) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      primary(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      ParseTree op = pt.getChild(UNARY_OPERATOR);
      switch (op.getKind()) {
        case NOT:
          unary(
              ast.insertSubTree(AstNodeKinds.NOT, Types.UNKNOWN, Types.UNKNOWN, op.token.line,
                  op.token.linePos),
              pt.getChild(UNARY), sTable);

          break;
        case MINUS:
          unary(
              ast.insertSubTree(AstNodeKinds.NEG, Types.INTEGER, Types.INTEGER, op.token.line,
                  op.token.linePos),
              pt.getChild(UNARY),
              sTable);

          break;

        default:
          throw new Error("Invalid Expression child count: " + pt.getChildCount());
      }

    }
  }

  void primary(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!isChildCount1or3(pt)) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      ParseTree tmp = pt.getChild(PRIMARY_VALUE);
      switch (pt.getChild(0).getKind()) {
        case V_BOOL:
          vbool(ast, tmp, sTable);
          break;
        case V_INT:
          vint(ast, tmp, sTable);

          break;
        case IDENT:
          ident(ast, tmp, sTable);

          break;

        default:
          break;
      }
    } else {
      expression(ast, pt.getChild(PRIMARY_EXPRESSION), sTable);
    }
  }

  void vbool(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ast.insertSubTree(AstNodeKinds.BOOLEAN, Types.BOOLEAN, Types.BOOLEAN, pt.token.line, pt.token.linePos,
        pt.token.lexem);

  }

  void vint(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ast.insertSubTree(AstNodeKinds.INTEGER, Types.INTEGER, Types.INTEGER, pt.token.line, pt.token.linePos,
        pt.token.lexem);
  }

  void ident(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!sTable.has(pt.token.lexem)) {
      throw new UnknownIdentifierError(pt.token);
    }
    Symbol s = sTable.get(pt.token.lexem);
    ast.insertSubTree(AstNodeKinds.IDENT, s.getType(), s.getType(), pt.token.line, pt.token.linePos,
        pt.token.lexem);
  }

  boolean isChildCount1or3(ParseTree pt) {
    return pt.getChildCount() == 1 || pt.getChildCount() == 3;
  }

  private void processProgram(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    for (int i = 0; i < pt.getChildCount(); i++) {
      statement(pt.getChild(i), ast, sTable);
    }
  }

  private void branch(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    ParseTree currentTree = pt.getChild(IF_STATEMENT);
    for (int i = IF_STATEMENT; currentTree.getKind() != TokenKind.CLOSE_PARANTHESES; currentTree = pt.getChild(++i)) {
      statement(currentTree, ast, sTable);
    }
  }

  private void elseBranch(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    int elseIndex = pt.getChildIndex(TokenKind.ELSE);
    int firstStatementIndex = pt.getChildIndex(TokenKind.STATEMENT, elseIndex);
    // empty else block
    if (firstStatementIndex == -1) {
      return;
    }
    ParseTree currentTree = pt.getChild(firstStatementIndex);
    for (int i = firstStatementIndex; currentTree.getKind() != TokenKind.CLOSE_PARANTHESES; currentTree = pt
        .getChild(++i)) {
      statement(currentTree, ast, sTable);
    }
  }

  private void statement(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    switch (pt.getChild(STATEMENT_TYPE).getKind()) {
      case IF:
        AbstractSyntaxTree if_node = ast.insertSubTree(AstNodeKinds.IF, Types.NONE, Types.NONE,
            pt.token.line,
            pt.token.linePos);
        ParseTree if_condition = pt.getChild(IF_EXPRESSION);
        expression(if_node.insertSubTree(AstNodeKinds.CONDITION, Types.BOOLEAN, Types.NONE,
            if_condition.token.line, if_condition.token.linePos), if_condition, sTable);
        branch(pt,
            if_node.insertSubTree(AstNodeKinds.BRANCH, Types.NONE, Types.NONE, pt.token.line,
                pt.token.linePos),
            sTable.getScopedSymbolTable());

        // check if else branch is present and if the else branch is not empty
        if (pt.getChildIndex(TokenKind.ELSE) != -1
            && pt.getChildIndex(TokenKind.ELSE) != pt.getChildCount() - EMPTY_BLOCK_SIZE - 1) {
          elseBranch(pt,
              if_node.insertSubTree(AstNodeKinds.BRANCH, Types.NONE, Types.NONE, pt.token.line,
                  pt.token.linePos),
              sTable.getScopedSymbolTable());
        }
        break;
      case WHILE:
        AbstractSyntaxTree while_node = ast.insertSubTree(AstNodeKinds.WHILE, Types.NONE, Types.NONE,
            pt.token.line,
            pt.token.linePos);
        ParseTree while_condition = pt.getChild(WHILE_EXPRESSION);
        expression(while_node.insertSubTree(AstNodeKinds.CONDITION, Types.BOOLEAN, Types.NONE,
            while_condition.token.line, while_condition.token.linePos), while_condition, sTable);
        branch(pt,
            while_node.insertSubTree(AstNodeKinds.BRANCH, Types.NONE, Types.NONE, pt.token.line,
                pt.token.linePos),
            sTable.getScopedSymbolTable());

        // check if else branch is present and if the else branch is not empty
        if (pt.getChildIndex(TokenKind.ELSE) != -1 && pt.getChildIndex(TokenKind.ELSE) != pt.getChildCount() - 3) {
          elseBranch(pt,
              while_node.insertSubTree(AstNodeKinds.BRANCH, Types.NONE, Types.NONE, pt.token.line,
                  pt.token.linePos),
              sTable.getScopedSymbolTable());
        }
        break;
      // Declarations
      case BOOL: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(
            ast.insertSubTree(AstNodeKinds.DECLARATION, Types.NONE, Types.NONE, pt.token.line,
                pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, Types.BOOLEAN, Types.NONE,
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      case INT: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(
            ast.insertSubTree(AstNodeKinds.DECLARATION, Types.NONE, Types.NONE, pt.token.line,
                pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, Types.INTEGER, Types.NONE,
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      // Assignment
      case IDENT:
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, Types.NONE, Types.NONE,
                pt.token.line, pt.token.linePos),
            pt, sTable);

        break;

      default:
        break;
    }
  }

}
