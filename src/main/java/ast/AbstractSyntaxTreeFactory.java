package ast;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Map.Entry;

import lexer.Token;
import parser.SyntaxTree;

public class AbstractSyntaxTreeFactory {
  public AbstractSyntaxTree ast;
  public SymbolTable sTable;
  final int STATEMENT_TYPE = 0;
  final int DECLARERATION_IDENTIFIER = 1;
  final int DECLARERATION_EXPRESSION = 3;
  final int OPERATOR = 2;

  public AbstractSyntaxTreeFactory() {
    ast = new AbstractSyntaxTree(AstNodeKinds.PROGRAM, AstNodeTypes.NONE, 0, 0);
    sTable = new SymbolTable();
  }

  public AbstractSyntaxTree fromSyntaxTree(SyntaxTree st) {
    processProgram(st, ast, sTable);
    return ast;
  }

  private void processProgram(SyntaxTree st, AbstractSyntaxTree ast, SymbolTable sTable) {
    for (int i = 0; i < st.getChildNumber(); i++) {
      statement(st.getChild(i), ast, sTable);
    }
  }

  private void statement(SyntaxTree st, AbstractSyntaxTree ast, SymbolTable sTable) {
    switch (st.getChild(STATEMENT_TYPE).getKind()) {
      case IF:

        break;

      case BOOL: {
        Token t = st.getChild(DECLARERATION_IDENTIFIER).token;
        String ident = t.lexem;
        if (sTable.has(ident)) {
          // TODO
        }
        int adress = sTable.insert(ident, AstNodeTypes.BOOLEAN);
        AbstractSyntaxTree tmp = ast.insertSubTree(AstNodeKinds.DECLARATION, AstNodeTypes.BOOLEAN, st.token.line,
            st.token.linePos);
        tmp.insertSubTree(AstNodeKinds.IDENT, AstNodeTypes.BOOLEAN, t.line, t.linePos, ident);
        tmp.insertSubTree(AstNodeKinds.ADRESS, AstNodeTypes.BOOLEAN, t.line, t.linePos, "" + adress);
        break;
      }
      case INT: {
        Token t = st.getChild(DECLARERATION_IDENTIFIER).token;
        String ident = t.lexem;
        if (sTable.has(ident)) {
          // TODO
        }
        int adress = sTable.insert(ident, AstNodeTypes.INTEGER);
        AbstractSyntaxTree tmp = ast.insertSubTree(AstNodeKinds.DECLARATION, AstNodeTypes.INTEGER, st.token.line,
            st.token.linePos);
        tmp.insertSubTree(AstNodeKinds.IDENT, AstNodeTypes.INTEGER, t.line, t.linePos, ident);
        tmp.insertSubTree(AstNodeKinds.ADRESS, AstNodeTypes.INTEGER, t.line, t.linePos, "" + adress);
        expression(ast, st.getChild(DECLARERATION_EXPRESSION), sTable);
        break;
      }
      case IDENT:

        break;

      default:
        break;
    }
  }

  void expression(AbstractSyntaxTree ast, SyntaxTree st, SymbolTable sTable) {
    if (!st.hasChilds()) {
      
    }
    if(st.getChildNumber()>1){
      switch (st.getChild(OPERATOR).getKind()) {
        case EQEQ:
          
          break;

        case NOTEQ:
          
          break;

        case LTEQ:
          
          break;

        case LT:
          
          break;

        case GTEQ:
          
          break;

        case GT:
          
          break;
        
        case LAND:
          
          break;

        case LOR:
          
          break;

        case OR:
          
          break;

        case AND:
          
          break;

        case PLUS:
          
          break;

        case MINUS:
          
          break;

        case ASTERISK:
          
          break;

        case SLASH:
          
          break;

        default:
          break;
      }
    }
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
    symbols = new Hashtable<String, Symbol>();
  }

  SymbolTable getScopedSymbolTable() {
    SymbolTable st = new SymbolTable(this, memoryOffset + symbols.size());
    children.add(st);
    return st;
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
