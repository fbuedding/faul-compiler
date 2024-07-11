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
import error.UnexpectedError;
import lexer.Token;
import lexer.TokenKind;
import parser.ParseTree;
import types.*;

public class AbstractSyntaxTreeFactory {
  public AbstractSyntaxTree ast;
  public SymbolTable symbolTable;

  public AbstractSyntaxTreeFactory() {
    ast = new AbstractSyntaxTree(AstNodeKinds.PROGRAM, Type.tVoid(), 0, 0);
    symbolTable = new SymbolTable();
    symbolTable.initStd();
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
    Type type = new Type(Types.VAR);
    Token declarationType = pt.getChild(DECLARATION_TYPE).token;
    switch (declarationType.lexem) {
      case "int":
        type.setRetType(Types.INTEGER);
        break;
      case "bool":
        type.setRetType(Types.BOOLEAN);
        break;

      default:
        throw new UnexpectedError("Every declaration should have a type", declarationType.line,
            declarationType.linePos);
    }
    int adress = sTable.insert(ident, type);
    ast.insertSubTree(AstNodeKinds.IDENT, type, t.line, t.linePos, ident);
    ast.insertSubTree(AstNodeKinds.ADDRESS, new Type(Types.MEMORY_ADDRESS, Types.MEMORY_ADDRESS), t.line, t.linePos,
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
    ast.type = new Type(s.getType().getRetType());
    // insert ident child
    ast.insertSubTree(AstNodeKinds.IDENT, new Type(Types.VAR, s.getType().getRetType()), ident.token.line,
        ident.token.linePos,
        ident.token.lexem);
    // handle expression
    expression(ast, pt.getChild(ASSIGNMENT_EXPRESSION), sTable);
  }

  void expression(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {

    if (!isChildCount1or3(pt)) {
      System.out.println(pt);
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }

    if (pt.getChildCount() == 1) {
      equality(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      AbstractSyntaxTree tmp;
      ParseTree op = pt.getChild(OPERATOR);
      switch (pt.getChild(OPERATOR).getKind()) {
        case LAND:
          tmp = ast.insertSubTree(AstNodeKinds.LAND, new Type(Types.BOOLEAN, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;

        case LOR:
          tmp = ast.insertSubTree(AstNodeKinds.LOR, new Type(Types.BOOLEAN, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;

        case OR:
          tmp = ast.insertSubTree(AstNodeKinds.OR, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;

        case AND:
          tmp = ast.insertSubTree(AstNodeKinds.AND, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);
          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator " + op.token.lexem, op.token.line, op.token.linePos);
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
          tmp = ast.insertSubTree(AstNodeKinds.NOTEQ, new Type(Types.UNKNOWN, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;
        case EQEQ:
          tmp = ast.insertSubTree(AstNodeKinds.EQEQ, new Type(Types.UNKNOWN, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator: " + op.token.lexem, op.token.line,
              op.token.linePos);
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
          tmp = ast.insertSubTree(AstNodeKinds.LT, new Type(Types.INTEGER, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;
        case LTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.LTEQ, new Type(Types.INTEGER, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;
        case GT:
          tmp = ast.insertSubTree(AstNodeKinds.GT, new Type(Types.INTEGER, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;
        case GTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.GTEQ, new Type(Types.INTEGER, Types.BOOLEAN), op.token.line,
              op.token.linePos);

          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator: " + op.token.lexem, op.token.line,
              op.token.linePos);
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
          tmp = ast.insertSubTree(AstNodeKinds.PLUS, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;
        case MINUS:
          tmp = ast.insertSubTree(AstNodeKinds.MINUS, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator: " + op.token.lexem, op.token.line,
              op.token.linePos);
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
          tmp = ast.insertSubTree(AstNodeKinds.MUL, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;
        case SLASH:
          tmp = ast.insertSubTree(AstNodeKinds.DIV, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;
        case PERCENT:
          tmp = ast.insertSubTree(AstNodeKinds.MOD, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
              op.token.linePos);

          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator: " + op.token.lexem, op.token.line,
              op.token.linePos);
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
              ast.insertSubTree(AstNodeKinds.NOT, new Type(Types.UNKNOWN, Types.UNKNOWN), op.token.line,
                  op.token.linePos),
              pt.getChild(UNARY), sTable);

          break;
        case MINUS:
          unary(
              ast.insertSubTree(AstNodeKinds.NEG, new Type(Types.INTEGER, Types.INTEGER), op.token.line,
                  op.token.linePos),
              pt.getChild(UNARY),
              sTable);

          break;

        default:
          throw new UnexpectedError("ParseTree malformed, Operator: " + op.token.lexem, op.token.line,
              op.token.linePos);
      }

    }
  }

  void primary(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    int childCount = pt.getChildCount();
    if (childCount < 1 || childCount > 3) {
      throw new UnexpectedError("ParseTree malformed, Childcount: " + childCount, pt.token.line,
          pt.token.linePos);
    }
    if (childCount == 1) {
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
    } else if (childCount == 3) {
      expression(ast, pt.getChild(PRIMARY_EXPRESSION), sTable);
    } else {
      functionCall(ast.insertSubTree(AstNodeKinds.FUNC_CALL, new Type(Types.FUNCTION, Types.UNKNOWN), pt.token.line,
          pt.token.linePos), pt, sTable);
    }
  }

  void vbool(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ast.insertSubTree(AstNodeKinds.BOOLEAN, new Type(Types.BOOLEAN, Types.BOOLEAN), pt.token.line, pt.token.linePos,
        pt.token.lexem);

  }

  void vint(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ast.insertSubTree(AstNodeKinds.INTEGER, new Type(Types.INTEGER, Types.INTEGER), pt.token.line, pt.token.linePos,
        pt.token.lexem);
  }

  void ident(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws CompileError {
    if (!sTable.has(pt.token.lexem)) {
      throw new UnknownIdentifierError(pt.token);
    }
    Symbol s = sTable.get(pt.token.lexem);
    ast.insertSubTree(AstNodeKinds.IDENT, s.getType(), pt.token.line, pt.token.linePos,
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
    TokenKind tk = pt.getChild(STATEMENT_TYPE).getKind();
    switch (tk) {
      case IF:
        AbstractSyntaxTree if_node = ast.insertSubTree(AstNodeKinds.IF, new Type(Types.VOID, Types.VOID),
            pt.token.line,
            pt.token.linePos);
        ParseTree if_condition = pt.getChild(IF_EXPRESSION);
        expression(if_node.insertSubTree(AstNodeKinds.CONDITION, new Type(Types.BOOLEAN, Types.VOID),
            if_condition.token.line, if_condition.token.linePos), if_condition, sTable);
        branch(pt,
            if_node.insertSubTree(AstNodeKinds.BRANCH, new Type(Types.VOID, Types.VOID), pt.token.line,
                pt.token.linePos),
            sTable.getScopedSymbolTable());

        // check if else branch is present and if the else branch is not empty
        if (pt.getChildIndex(TokenKind.ELSE) != -1
            && pt.getChildIndex(TokenKind.ELSE) != pt.getChildCount() - EMPTY_BLOCK_SIZE - 1) {
          elseBranch(pt,
              if_node.insertSubTree(AstNodeKinds.BRANCH, new Type(Types.VOID, Types.VOID), pt.token.line,
                  pt.token.linePos),
              sTable.getScopedSymbolTable());
        }
        break;
      case WHILE:
        AbstractSyntaxTree while_node = ast.insertSubTree(AstNodeKinds.WHILE, new Type(Types.VOID, Types.VOID),
            pt.token.line,
            pt.token.linePos);
        ParseTree while_condition = pt.getChild(WHILE_EXPRESSION);
        expression(while_node.insertSubTree(AstNodeKinds.CONDITION, new Type(Types.BOOLEAN, Types.VOID),
            while_condition.token.line, while_condition.token.linePos), while_condition, sTable);
        branch(pt,
            while_node.insertSubTree(AstNodeKinds.BRANCH, new Type(Types.VOID, Types.VOID), pt.token.line,
                pt.token.linePos),
            sTable.getScopedSymbolTable());

        // check if else branch is present and if the else branch is not empty
        if (pt.getChildIndex(TokenKind.ELSE) != -1 && pt.getChildIndex(TokenKind.ELSE) != pt.getChildCount() - 3) {
          elseBranch(pt,
              while_node.insertSubTree(AstNodeKinds.BRANCH, new Type(Types.VOID, Types.VOID), pt.token.line,
                  pt.token.linePos),
              sTable.getScopedSymbolTable());
        }
        break;
      // Declarations
      case BOOL: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(
            ast.insertSubTree(AstNodeKinds.DECLARATION, new Type(Types.VOID, Types.VOID), pt.token.line,
                pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, new Type(Types.BOOLEAN, Types.VOID),
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      case INT: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(
            ast.insertSubTree(AstNodeKinds.DECLARATION, new Type(Types.VOID, Types.VOID), pt.token.line,
                pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, new Type(Types.INTEGER, Types.VOID),
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      // Assignment
      case IDENT:
        if (pt.getChildIndex(TokenKind.FUNC_CALL) != -1) {
          functionCall(ast.insertSubTree(AstNodeKinds.FUNC_CALL, new Type(Types.FUNCTION, Types.UNKNOWN),
              pt.token.line, pt.token.linePos), pt, sTable);

        } else {
          assignment(
              ast.insertSubTree(AstNodeKinds.ASSIGNMENT, new Type(Types.VOID, Types.VOID),
                  pt.token.line, pt.token.linePos),
              pt, sTable);
        }

        break;

      default:
        throw new UnexpectedError("Unexpected statement type: " + tk, pt.token.line, pt.token.linePos);
    }
  }

  private void functionCall(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) throws UnknownIdentifierError {
    Token t = pt.getChild(0).token;
    Symbol s = sTable.get(t.lexem);
    if (s == null) {
      throw new UnknownIdentifierError(t);
    }
    ast.insertSubTree(AstNodeKinds.IDENT, s.getType(), t.line, t.linePos, t.lexem);
  }

}
