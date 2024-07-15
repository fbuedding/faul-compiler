package parser;

import java.util.Iterator;
import java.util.LinkedList;

import lexer.Token;
import lexer.TokenKind;

public class ParseTree {
  public Token token;
  LinkedList<ParseTree> childNodes;

  public ParseTree() {
    childNodes = new LinkedList<ParseTree>();
    this.token = new Token(TokenKind.PROGRAM, "", 0, 0);
  }

  public ParseTree(Token t) {
    childNodes = new LinkedList<ParseTree>();
    this.token = t;
  }

  public String toString() {
    StringBuilder buffer = new StringBuilder(50);
    print(buffer, "", "");
    return buffer.toString();
  }

  public void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append(this.token.kind);
    if (!this.token.lexem.equals("")) {
      buffer.append(": " + this.token.lexem);

    }
    buffer.append('\n');
    for (Iterator<ParseTree> it = childNodes.iterator(); it.hasNext();) {
      ParseTree next = it.next();
      if (it.hasNext()) {
        next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
      } else {
        next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
      }
    }
  }

  public TokenKind getKind() {
    return token.kind;
  }

  public boolean hasChilds() {
    return this.childNodes.size() > 0;
  }

  /**
   * Returns the first index of child with given kind, starting at the first child
   * 
   * @param kind TokenKind to be looked for
   * @return index of the child or -1 if not found
   */
  public int getChildIndex(TokenKind kind) {
    return getChildIndex(kind, 0);
  }

  /**
   * Returns the first index of child with given kind
   * 
   * @param kind   TokenKind to be looked for
   * @param offset offset from where the search starts
   * @return index of the child or -1 if not found
   */
  public int getChildIndex(TokenKind kind, int offset) {
    if (!this.hasChilds()) {
      return -1;
    }
    for (int i = offset; i < this.childNodes.size(); i++) {
      if (this.getChild(i).getKind() == kind) {
        return i;
      }
    }
    return -1;
  }

  public ParseTree getChild(int i) {
    if (i > this.childNodes.size() || i < 0)
      return null;
    else
      return this.childNodes.get(i);
  }

  /**
   * This removes the first child element and returns itself. Convenient for
   * turning a declaration into an assignment.
   * 
   * @return
   */
  public ParseTree removeFirst() {
    // e
    this.childNodes.removeFirst();
    return this;
  }

  public int getChildCount() {
    return childNodes.size();
  }

  void printParseTree(int tabs) {
    for (int i = 0; i < tabs; i++) {
      System.out.print("  ");
    }
    for (int i = 0; i < this.childNodes.size(); i++) {
      this.childNodes.get(i).printParseTree(tabs + 1);
    }
  }

  void buildStringParseTree(int tabs, StringBuilder sb, String indentation) {
    for (int i = 0; i < tabs; i++) {
      sb.append(indentation);
    }
    sb.append(token + "\n");
    for (int i = 0; i < this.childNodes.size(); i++) {
      this.childNodes.get(i).buildStringParseTree(tabs + 1, sb, indentation);
    }
  }

  ParseTree insertSubtree(Token t) {
    ParseTree node;
    node = new ParseTree(t);
    this.childNodes.addLast(node);
    return node;
  }

  LinkedList<ParseTree> getChildNodes() {
    return this.childNodes;
  }
}
