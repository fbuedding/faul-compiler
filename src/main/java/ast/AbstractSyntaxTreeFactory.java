package ast;

import static parser.Constants.ASSIGNMENT_EXPRESSION;
import static parser.Constants.ASSIGNMENT_IDENTIFIER;
import static parser.Constants.DECLARATION_TYPE;
import static parser.Constants.DECLARERATION_EXPRESSION;
import static parser.Constants.DECLARERATION_IDENTIFIER;
import static parser.Constants.FIRST_OPERAND;
import static parser.Constants.IF_EXPRESSION;
import static parser.Constants.IF_STATEMENT;
import static parser.Constants.OPERATOR;
import static parser.Constants.PRIMARY_EXPRESSION;
import static parser.Constants.PRIMARY_VALUE;
import static parser.Constants.SECOND_OPERAND;
import static parser.Constants.STATEMENT_TYPE;
import static parser.Constants.UNARY_OPERATOR;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import lexer.Token;
import lexer.TokenKind;
import parser.ParseTree;

public class AbstractSyntaxTreeFactory {
  public AbstractSyntaxTree ast;
  public SymbolTable sTable;

  public AbstractSyntaxTreeFactory() {
    ast = new AbstractSyntaxTree(AstNodeKinds.PROGRAM, AstNodeTypes.NONE, 0, 0);
    sTable = new SymbolTable();
  }

  public AbstractSyntaxTree fromParseTree(ParseTree st) {
    processProgram(st, ast, sTable);
    return ast;
  }

  private void processProgram(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) {
    for (int i = 0; i < pt.getChildCount(); i++) {
      statement(pt.getChild(i), ast, sTable);
    }
  }

  private void branch(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) {
    ParseTree currentTree = pt.getChild(IF_STATEMENT);
    for (int i = IF_STATEMENT; currentTree.getKind() != TokenKind.CLOSE_PARANTHESES; currentTree = pt.getChild(++i)) {
      statement(currentTree, ast, sTable);
    }
  }

  private void elseBranch(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) {
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

  private void statement(ParseTree pt, AbstractSyntaxTree ast, SymbolTable sTable) {
    switch (pt.getChild(STATEMENT_TYPE).getKind()) {
      case IF:
        AbstractSyntaxTree if_node = ast.insertSubTree(AstNodeKinds.IF, AstNodeTypes.NONE, pt.token.line,
            pt.token.linePos);
        ParseTree if_condition = pt.getChild(IF_EXPRESSION);
        expression(if_node.insertSubTree(AstNodeKinds.CONDITION, AstNodeTypes.BOOLEAN,
            if_condition.token.line, if_condition.token.linePos), if_condition, sTable);
        branch(pt, if_node.insertSubTree(AstNodeKinds.BRANCH, AstNodeTypes.NONE, pt.token.line, pt.token.linePos),
            sTable.getScopedSymbolTable());

        // check if else branch is present and if the else branch is not empty
        if (pt.getChildIndex(TokenKind.ELSE) != -1 && pt.getChildIndex(TokenKind.ELSE) != pt.getChildCount() - 3) {
          elseBranch(pt, if_node.insertSubTree(AstNodeKinds.BRANCH, AstNodeTypes.NONE, pt.token.line, pt.token.linePos),
              sTable.getScopedSymbolTable());
        }
        break;
      // Declarations
      case BOOL: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(ast.insertSubTree(AstNodeKinds.DECLARATION, AstNodeTypes.NONE, pt.token.line, pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, AstNodeTypes.NONE,
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      case INT: {
        ParseTree expression = pt.getChild(DECLARERATION_EXPRESSION);
        declaration(ast.insertSubTree(AstNodeKinds.DECLARATION, AstNodeTypes.NONE, pt.token.line, pt.token.linePos),
            pt, sTable);
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, AstNodeTypes.NONE,
                expression.token.line, expression.token.linePos),
            pt.removeFirst(), sTable);
        break;
      }
      // Assignment
      case IDENT:
        assignment(
            ast.insertSubTree(AstNodeKinds.ASSIGNMENT, AstNodeTypes.NONE,
                pt.token.line, pt.token.linePos),
            pt, sTable);

        break;

      default:
        break;
    }
  }

  /**
   * Handle a declaration, this isn't a non terminal of the grammar, but is
   * implicit
   * 
   * @param ast
   * @param pt
   * @param sTable
   */
  void declaration(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    Token t = pt.getChild(DECLARERATION_IDENTIFIER).token;
    String ident = t.lexem;
    if (sTable.has(ident)) {
      // TODO
    }
    AstNodeTypes type;
    switch (pt.getChild(DECLARATION_TYPE).token.lexem) {
      case "int":
        type = AstNodeTypes.INTEGER;
        break;
      case "bool":
        type = AstNodeTypes.BOOLEAN;
        break;

      default:
        // TODO: Meaningfull Error
        throw new Error("Every declaration should have a type");
    }
    int adress = sTable.insert(ident, type);
    ast.insertSubTree(AstNodeKinds.IDENT, type, t.line, t.linePos, ident);
    ast.insertSubTree(AstNodeKinds.ADRESS, AstNodeTypes.MEMORY_ADDRESS, t.line, t.linePos, "" + adress);
  }

  /**
   * Handle an assignment, this isn't a non terminal of the grammar, but is
   * implicit
   * 
   * @param ast
   * @param pt
   * @param sTable
   */
  void assignment(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ParseTree ident = pt.getChild(ASSIGNMENT_IDENTIFIER);
    ast.insertSubTree(AstNodeKinds.IDENT, AstNodeTypes.NONE, ident.token.line, ident.token.linePos, ident.token.lexem);
    expression(ast, pt.getChild(ASSIGNMENT_EXPRESSION), sTable);
  }

  void expression(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {

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
          tmp = ast.insertSubTree(AstNodeKinds.LAND, AstNodeTypes.BOOLEAN, op.token.line, op.token.linePos);

          break;

        case LOR:
          tmp = ast.insertSubTree(AstNodeKinds.LOR, AstNodeTypes.BOOLEAN, op.token.line, op.token.linePos);

          break;

        case OR:
          tmp = ast.insertSubTree(AstNodeKinds.OR, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;

        case AND:
          tmp = ast.insertSubTree(AstNodeKinds.AND, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);
          break;

        default:
          // TODO: Create real Error
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      equality(tmp, pt.getChild(FIRST_OPERAND), sTable);
      expression(tmp, pt.getChild(SECOND_OPERAND), sTable);
    }
  }

  void equality(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
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
          tmp = ast.insertSubTree(AstNodeKinds.NOTEQ, AstNodeTypes.BOOLEAN, op.token.line, op.token.linePos);

          break;
        case EQEQ:
          tmp = ast.insertSubTree(AstNodeKinds.EQEQ, AstNodeTypes.BOOLEAN, op.token.line, op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      comparision(tmp, pt.getChild(FIRST_OPERAND), sTable);
      equality(tmp, pt.getChild(SECOND_OPERAND), sTable);
    }

  }

  void comparision(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
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
          tmp = ast.insertSubTree(AstNodeKinds.LT, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;
        case LTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.LTEQ, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;
        case GT:
          tmp = ast.insertSubTree(AstNodeKinds.GT, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;
        case GTEQ:
          tmp = ast.insertSubTree(AstNodeKinds.GTEQ, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      arithmeticExpr(tmp, pt.getChild(FIRST_OPERAND), sTable);
      comparision(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void arithmeticExpr(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
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
          tmp = ast.insertSubTree(AstNodeKinds.PLUS, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;
        case MINUS:
          tmp = ast.insertSubTree(AstNodeKinds.MINUS, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      term(tmp, pt.getChild(FIRST_OPERAND), sTable);
      arithmeticExpr(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void term(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
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
          tmp = ast.insertSubTree(AstNodeKinds.ASTERISK, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;
        case SLASH:
          tmp = ast.insertSubTree(AstNodeKinds.SLASH, AstNodeTypes.INTEGER, op.token.line, op.token.linePos);

          break;

        default:
          throw new Error("Unexpected Operator " + op.token.lexem);
      }
      unary(tmp, pt.getChild(FIRST_OPERAND), sTable);
      term(tmp, pt.getChild(SECOND_OPERAND), sTable);

    }
  }

  void unary(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    if (pt.getChildCount() != 1 && pt.getChildCount() != 2) {
      throw new Error("Invalid Expression child count: " + pt.getChildCount());
    }
    if (pt.getChildCount() == 1) {
      primary(ast, pt.getChild(FIRST_OPERAND), sTable);
    } else {
      ParseTree op = pt.getChild(UNARY_OPERATOR);
      switch (op.getKind()) {
        case NOT:
          unary(ast.insertSubTree(AstNodeKinds.NOT, AstNodeTypes.BOOLEAN, op.token.line, op.token.linePos), pt, sTable);

          break;
        case MINUS:
          unary(ast.insertSubTree(AstNodeKinds.MINUS, AstNodeTypes.INTEGER, op.token.line, op.token.linePos), pt,
              sTable);

          break;

        default:
          throw new Error("Invalid Expression child count: " + pt.getChildCount());
      }

    }
  }

  void primary(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
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
    ast.insertSubTree(AstNodeKinds.BOOLEAN, AstNodeTypes.BOOLEAN, pt.token.line, pt.token.linePos, pt.token.lexem);

  }

  void vint(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    ast.insertSubTree(AstNodeKinds.INTEGER, AstNodeTypes.INTEGER, pt.token.line, pt.token.linePos, pt.token.lexem);
  }

  void ident(AbstractSyntaxTree ast, ParseTree pt, SymbolTable sTable) {
    if (!sTable.has(pt.token.lexem)) {
      throw new Error("Undefined variable " + pt.token.lexem);
    }
    ast.insertSubTree(AstNodeKinds.IDENT, sTable.get(pt.token.lexem).ant, pt.token.line, pt.token.linePos,
        pt.token.lexem);
  }

  boolean isChildCount1or3(ParseTree pt) {
    return pt.getChildCount() == 1 || pt.getChildCount() == 3;
  }

}

class SymbolTable {
  int memoryOffset = 0;
  SymbolTable parent = null;
  Vector<SymbolTable> children = new Vector<SymbolTable>();
  Hashtable<String, Symbol> symbols;

  SymbolTable() {
    symbols = new Hashtable<String, Symbol>();
  }

  SymbolTable(SymbolTable parent, int memoryOffset) {
    this.parent = parent;
    this.memoryOffset = memoryOffset;
    symbols = new Hashtable<String, Symbol>();

  }

  SymbolTable getScopedSymbolTable() {
    SymbolTable st = new SymbolTable(this, (memoryOffset + symbols.size()));
    children.add(st);
    return st;
  }

  SymbolTable getNextScopedSymbolTable() {
    return children.removeFirst();
  }

  int insert(String name, AstNodeTypes ant) {
    int adress = memoryOffset + symbols.size();
    symbols.put(name, new Symbol(ant, adress));
    return adress;
  }

  boolean has(String name) {
    if (symbols.containsKey(name)) {
      return true;
    }

    if (parent == null) {
      return false;
    }
    return parent.has(name);
  }

  Symbol get(String name) {
    Symbol s = this.symbols.get(name);
    if (s != null) {
      return s;
    }

    if (this.parent != null) {
      return this.parent.get(name);
    }

    return null;

  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    print(buffer, 0);
    return buffer.toString();
  }

  private void print(StringBuilder buffer, int indentation) {
    for (Entry<String, Symbol> entry : symbols.entrySet()) {
      for (int i = 0; i < indentation; i++) {
        buffer.append("  ");
      }
      buffer.append(entry.getKey());
      buffer.append(":  ");
      buffer.append(String.format("Type: %s, Memory-Adress: %d", entry.getValue().ant, entry.getValue().adress));
      buffer.append("\n");
    }
    indentation++;
    for (SymbolTable sTable : children) {
      sTable.print(buffer, indentation);
    }
  }
}

class Symbol {
  AstNodeTypes ant;

  int adress;

  Symbol(AstNodeTypes ant, int adress) {
    this.ant = ant;
    this.adress = adress;
  }
}
