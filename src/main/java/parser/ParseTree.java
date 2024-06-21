package parser;

import java.util.Iterator;
import java.util.LinkedList;


import lexer.Token;
import lexer.TokenKind;

public class ParseTree {
  public Token token;
  LinkedList<ParseTree> childNodes;

  public ParseTree(Token t) {
    childNodes = new LinkedList<ParseTree>();
    this.token = t;
  }

  void printParseTree(int tabs) {
    for (int i = 0; i < tabs; i++) {
      System.out.print("  ");
    }
    System.out.println(token);
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

  ParseTree insertSubtree(Token t) {
    ParseTree node;
    node = new ParseTree(t);
    this.childNodes.addLast(node);
    return node;
  }

  public boolean hasChilds() {
    return this.childNodes.size() > 0;
  }

  public ParseTree getChild(int i) {
    if (i > this.childNodes.size())
      return null;
    else
      return this.childNodes.get(i);
  }

  LinkedList<ParseTree> getChildNodes() {
    return this.childNodes;
  }

  public int getChildNumber() {
    return childNodes.size();
  }
}
