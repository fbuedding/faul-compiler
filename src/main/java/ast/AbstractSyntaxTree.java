package ast;

import java.util.Iterator;
import java.util.LinkedList;

import types.*;

/**
 * Abstract Syntax Tree a simplified and annotated version of the
 * {@link parser.ParseTree}
 */
public class AbstractSyntaxTree {
  public AstNodeKinds kind;
  public Type type;
  public LinkedList<AbstractSyntaxTree> children;

  public int line;
  public int linePos;

  public String value = null;

  public AbstractSyntaxTree(AstNodeKinds k, Type type, int line, int linePos) {
    this.kind = k;
    this.type = type;
    this.line = line;
    this.linePos = linePos;
    children = new LinkedList<>();
  }

  public LinkedList<AbstractSyntaxTree> getChildren() {
    return children;
  }

  public int getChildrenCount() {
    return children.size();
  }

  public AstNodeKinds getKind() {
    return this.kind;
  }

  public void checkTypes() throws TypeError {
    switch (type.getType()) {
      case MEMORY_ADDRESS:
        // Do nothing should be a leaf
        return;
      case VOID: {
        for (AbstractSyntaxTree abstractSyntaxTree : children) {
          abstractSyntaxTree.checkTypes();
        }

        break;
      }
      case UNKNOWN:
        // ZumBeispiel bei <!> Operator der Fall
        if (hasChildren()) {
          for (AbstractSyntaxTree child : children) {
            child.checkTypes();
            // Set the type
            type.inferType(child.type);
            // For example the <==> and <!=> operator, they already have a rT, which is
            // bool. Otherwise, if UNKNOWN set the rT
            if (type.isRetUnknown()) {
              type.setRetType(type);
            }
          }
        }

        break;
      default:
        for (AbstractSyntaxTree child : children) {
          child.checkTypes();

          // Special function case where only the Return Type is unknow, since the return
          // type is encoded in the symbol
          if (type.isRetUnknown() && type.getType() == Types.FUNCTION) {
            type.setRetType(child.type.getType());
          }
          if (!type.checkType(child.type)) {
            throw new TypeError(type, child.type, child.line, child.linePos);
          }

        }
        break;

    }

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

  public AbstractSyntaxTree insertSubTree(AstNodeKinds k, Type t, int line, int linePos) {
    AbstractSyntaxTree node = new AbstractSyntaxTree(k, t, line, linePos);
    children.add(node);
    return node;
  }

  public AbstractSyntaxTree insertSubTree(AstNodeKinds k, Type t, int line, int linePos,
      String value) {
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
    buffer.append(", Type: " + type);
    buffer.append(",  Position: " + line + ":" + linePos);

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

  private boolean hasChildren() {
    return !children.isEmpty();
  }
}
