package ast;

import java.util.Iterator;
import java.util.LinkedList;

public class AbstractSyntaxTree {
  public AstNodeKinds kind;
  public AstNodeTypes type;
  public LinkedList<AbstractSyntaxTree> children;
  int line;
  int linePos;
  String value = null;

  public AbstractSyntaxTree(AstNodeKinds k, AstNodeTypes t, int line, int linePos) {
    this.kind = k;
    this.type = t;
    this.line = line;
    this.linePos = linePos;
    children = new LinkedList<>();
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
