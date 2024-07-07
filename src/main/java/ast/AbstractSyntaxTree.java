package ast;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Abstract Syntax Tree a simplified and annotated version of the
 * {@link parser.ParseTree}
 */
public class AbstractSyntaxTree {
  public AstNodeKinds kind;
  public AstNodeTypes type;
  public LinkedList<AbstractSyntaxTree> children;
  int line;
  int linePos;
  public String value = null;

  public AstNodeKinds getKind() {
    return this.kind;
  }

  public AbstractSyntaxTree(AstNodeKinds k, AstNodeTypes t, int line, int linePos) {
    this.kind = k;
    this.type = t;
    this.line = line;
    this.linePos = linePos;
    children = new LinkedList<>();
  }

  public int depth() {
    if (children.size() == 0) {
      return 0;

    }
    int depth = 0;
    for (AbstractSyntaxTree abstractSyntaxTree : children) {
      if (abstractSyntaxTree.depth() > depth) {
       depth = abstractSyntaxTree.depth(); 
      }
    }
    return depth + 1;

  }

  public AbstractSyntaxTree insertSubTree(AstNodeKinds k, AstNodeTypes t, int line, int linePos) {
    AbstractSyntaxTree node = new AbstractSyntaxTree(k, t, line, linePos);
    children.add(node);
    return node;
  }

  public AbstractSyntaxTree insertSubTree(AstNodeKinds k, AstNodeTypes t, int line, int linePos, String value) {
    AbstractSyntaxTree node = new AbstractSyntaxTree(k, t, line, linePos);
    node.value = value;
    children.add(node);
    return node;
  }

  public AbstractSyntaxTree getChild(int i) {
    return this.children.get(i);
  }

  public AbstractSyntaxTree getChild(AstNodeKinds kind) {
    for (AbstractSyntaxTree abstractSyntaxTree : children) {
      if (abstractSyntaxTree.kind == kind) {
        return abstractSyntaxTree;
      }
    }
    return null;
  }

  @Override
  public String toString() {
    StringBuilder buffer = new StringBuilder(50);
    print(buffer, "", "");
    return buffer.toString();
  }

  public void print(StringBuilder buffer, String prefix, String childrenPrefix) {
    buffer.append(prefix);
    buffer.append(kind);
    if (value != null) {
      buffer.append(": " + value);

    }
    buffer.append('\n');
    for (Iterator<AbstractSyntaxTree> it = children.iterator(); it.hasNext();) {
      AbstractSyntaxTree next = it.next();
      if (it.hasNext()) {
        next.print(buffer, childrenPrefix + "├── ", childrenPrefix + "│   ");
      } else {
        next.print(buffer, childrenPrefix + "└── ", childrenPrefix + "    ");
      }
    }
  }
}
