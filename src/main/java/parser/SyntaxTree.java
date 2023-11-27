package parser;

import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.tree.TreeNode;

import lexer.Token;

public class SyntaxTree {
  Token token;
  LinkedList<SyntaxTree> childNodes;

  public SyntaxTree(Token t) {
    childNodes = new LinkedList<SyntaxTree>();
    this.token = t;
  }

  void printSyntaxTree(int tabs) {
    for (int i = 0; i < tabs; i++) {
      System.out.print("  ");
    }
    System.out.println(token);
    for (int i = 0; i < this.childNodes.size(); i++) {
      this.childNodes.get(i).printSyntaxTree(tabs + 1);
    }
  }

  void buildStringSyntaxTree(int tabs, StringBuilder sb, String indentation) {
    for (int i = 0; i < tabs; i++) {
      sb.append(indentation);
    }
    sb.append(token + "\n");
    for (int i = 0; i < this.childNodes.size(); i++) {
      this.childNodes.get(i).buildStringSyntaxTree(tabs + 1, sb, indentation);
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
    for (Iterator<SyntaxTree> it = childNodes.iterator(); it.hasNext();) {
      SyntaxTree next = it.next();
      if (it.hasNext()) {
        next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
      } else {
        next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
      }
    }
  }

  SyntaxTree insertSubtree(Token t) {
    SyntaxTree node;
    node = new SyntaxTree(t);
    this.childNodes.addLast(node);
    return node;
  }

  SyntaxTree getChild(int i) {
    if (i > this.childNodes.size())
      return null;
    else
      return this.childNodes.get(i);
  }

  LinkedList<SyntaxTree> getChildNodes() {
    return this.childNodes;
  }

  int getChildNumber() {
    return childNodes.size();
  }
}
