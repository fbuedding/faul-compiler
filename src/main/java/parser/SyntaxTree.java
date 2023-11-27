package parser;

import java.util.LinkedList;

import lexer.Token;

public class SyntaxTree {
  Token token;
  LinkedList<SyntaxTree> childNodes;

  public SyntaxTree(Token t) {
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
