package ast;

import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

/**
 * SymbolTable
 */
public class SymbolTable {
  int memoryOffset = 0;
  SymbolTable parent = null;
  Vector<SymbolTable> children = new Vector<SymbolTable>();
  Hashtable<String, Symbol> symbols;

  public SymbolTable() {
    symbols = new Hashtable<String, Symbol>();
  }

  SymbolTable(SymbolTable parent, int memoryOffset) {
    this.parent = parent;
    this.memoryOffset = memoryOffset;
    symbols = new Hashtable<String, Symbol>();

  }

  public SymbolTable popScopedSymbolTable() {
    return children.removeFirst();
  }

  public int getAddress(String name) {
    Symbol s = get(name);

    if (s == null) {
      return -1;
    }

    return s.adress + this.memoryOffset;

  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder();
    print(buffer, 0);
    return buffer.toString();
  }

  SymbolTable getScopedSymbolTable() {
    SymbolTable st = new SymbolTable(this, (memoryOffset + symbols.size()));
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

  public AstNodeTypes getType() {
    return ant;
  }
}
