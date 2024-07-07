package emitter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import ast.AbstractSyntaxTree;
import ast.AstNodeKinds;
import ast.SymbolTable;

/**
 * Emmiter
 */
public class Emitter {
  public class Memory {
  }

  enum Register {

    T("t"),
    S("s"),
    V("v");

    public final String label;

    private Register(String label) {
      this.label = label;
    }
  }

  StringBuilder code;
  int tempCount = 0;
  int valCount = 0;
  int savedCount = 0;
  AbstractSyntaxTree ast;
  SymbolTable sTable;
  int wordLength = 0;

  String memOffsetReg = "";

  // Register lists for loading of variables

  final int maxLoaded = 8;
  LinkedHashMap<Integer, String> loaded = new LinkedHashMap<>();
  LinkedList<String> available = new LinkedList<String>(
      Arrays.asList("s0", "s1", "s2", "s3", "s4", "s5", "s6", "s7"));

  public Emitter(AbstractSyntaxTree ast, SymbolTable sTable) {
    this(ast, sTable, 4, true);
  }

  public Emitter(AbstractSyntaxTree ast, SymbolTable sTable, int wordLength, boolean has$Gp) {
    this.ast = ast;
    this.sTable = sTable;
    this.wordLength = wordLength;
    code = new StringBuilder("main:\n");
    if (has$Gp) {
      memOffsetReg = "$gp";
    } else {

      memOffsetReg = "$0";

    }
  }

  public void generate() {
    generate(ast, sTable);
  }

  private String initWord(int address) {
    String reg = "";
    if (!available.isEmpty()) {
      reg = available.pop();
    } else {
      reg = freeLoadedWord();
    }
    loaded.putFirst(address, reg);
    emit("# initializing %s from address %d", reg, address * wordLength);
    emit("move $%s, $0", reg);
    return reg;
  }

  private String loadWord(int address) {
    String reg = loaded.remove(address);
    if (reg == null && available.size() > 0) {
      reg = available.pop();
      emit("# loading %s from address %d", reg, address * wordLength);
      emitLw(reg, address);
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedWord();
    }
    loaded.putFirst(address, reg);

    return reg;

  }

  private void assignWord(int address, String thatReg) {
    String reg = loaded.remove(address);
    if (reg == null && !available.isEmpty()) {
      reg = available.pop();
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedWord();
    }
    loaded.putFirst(address, reg);

    emit("move $%s, $%s", reg, thatReg);

  }

  private String freeLoadedWord() {
    Entry<Integer, String> entry = loaded.pollLastEntry();
    String reg = entry.getValue();
    int address = entry.getKey();
    emit("# unloading %s and storing in address %d", reg, address * wordLength);
    emitSw(reg, address);
    return reg;

  }

  private void emit(String s) {
    code.append(s);
    code.append("\n");
  }

  private void emit(String s, Object... args) {
    code.append(String.format(s, args));
    code.append("\n");
  }

  private void emitLw(String reg, int address) {
    emit("lw $%s, %d(%s)", reg, address * wordLength, memOffsetReg);
  }

  private void emitSw(String reg, int address) {
    emit("sw $%s, %d(%s)", reg, address * wordLength, memOffsetReg);
  }

  private void emitInitVar(AbstractSyntaxTree ast) {
    int address = Integer.parseInt(ast.value);
    // loadWordIntoReg(address);
    // emit("sw $0, %d(%s)", address, memOffsetReg);
    initWord(address);

  }

  private void generate(AbstractSyntaxTree ast, SymbolTable sTable) {
    for (AbstractSyntaxTree currentAst : ast.children) {
      switch (currentAst.kind) {
        case DECLARATION:
          emitInitVar(currentAst.getChild(AstNodeKinds.ADDRESS));
          break;
        case ASSIGNMENT:
          assignment(currentAst, sTable);
          break;

        default:
          break;
      }
    }
  }

  private void assignment(AbstractSyntaxTree ast, SymbolTable sTable) {
    String ident = ast.getChild(AstNodeKinds.IDENT).value;
    AbstractSyntaxTree secondChild = ast.getChild(1);
    String reg = "";
    if (isBinOp(secondChild)) {
      reg = op(secondChild, sTable);
    } else if (isUnOp(secondChild)) {

    } else if (isVal(secondChild)) {
      reg = getReg(Register.T);
      switch (secondChild.kind) {
        case BOOLEAN:
          if (secondChild.value.equals("true")) {
            emit("addi $%s, $0, 1", reg);
          } else {
            emit("addi $%s, $0, 0", reg);
          }
          break;
        case INTEGER:
          int val = Integer.parseInt(secondChild.value);
          emit("addi $%s, $0, %d", reg, val);
          break;

        default:
          break;
      }
    }
    int address = sTable.getAddress(ident);
    decrementRegCounter(reg);
    assignWord(address, reg);
    // emit("sw $%s, %d(%s)", reg, address, memOffsetReg);
  }

  private String op(AbstractSyntaxTree ast, SymbolTable sTable) {
    AbstractSyntaxTree leftChild = ast.getChild(0);
    AbstractSyntaxTree rightChild = ast.getChild(1);
    String leftReg = "", rightReg = "";
    if (leftChild.depth() >= rightChild.depth()) {
      leftReg = handleChild(leftChild, sTable);
      rightReg = handleChild(rightChild, sTable);
    } else {
      rightReg = handleChild(rightChild, sTable);
      leftReg = handleChild(leftChild, sTable);
    }
    decrementRegCounter(leftReg, rightReg);
    String tempReg = getReg(Register.T);
    switch (ast.getKind()) {
      case PLUS:
        emit("add $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case MINUS:
        emit("sub $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case MUL:
        emit("mul $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case DIV:
        emit("div $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;

      default:
        break;
    }
    return tempReg;
  }

  private String id(AbstractSyntaxTree ast, SymbolTable sTable) {
    int address = sTable.getAddress(ast.value) * wordLength;
    String reg = loadWord(address);
    return reg;
  }

  private String handleChild(AbstractSyntaxTree ast, SymbolTable sTable) {
    String reg = "";
    if (isBinOp(ast)) {
      reg = op(ast, sTable);
    } else if (isUnOp(ast)) {

    } else if (isId(ast)) {
      reg = id(ast, sTable);

    } else if (isVal(ast)) {
      reg = val(ast, sTable);
    }
    return reg;
  }

  private String val(AbstractSyntaxTree ast, SymbolTable sTable) {
    String reg = getReg(Register.V);
    emit("li $%s, %s", reg, ast.value);
    return reg;

  }

  private void decrementRegCounter(String... regs) {
    for (String reg : regs) {
      if (reg.startsWith("t")) {
        tempCount--;
      } else if (reg.startsWith("s")) {
        savedCount--;
      } else if (reg.startsWith("v")) {
        valCount--;
      }
    }
  }

  private String getReg(Register r) {
    switch (r) {
      case T:
        return String.format("%s%d", r.label, tempCount++);
      case S:
        return String.format("%s%d", r.label, savedCount++);

      case V:
        return String.format("%s%d", r.label, valCount++);
      default:
        throw new Error("Should not happen");

    }

  }

  private boolean isId(AbstractSyntaxTree ast) {
    if (ast == null) {
      return false;
    }
    AstNodeKinds kind = ast.kind;
    return kind == AstNodeKinds.IDENT;
  }

  private boolean isVal(AbstractSyntaxTree ast) {
    if (ast == null) {
      return false;
    }
    AstNodeKinds kind = ast.kind;
    return kind == AstNodeKinds.INTEGER
        || kind == AstNodeKinds.BOOLEAN;
  }

  private boolean isUnOp(AbstractSyntaxTree ast) {
    if (ast == null) {
      return false;
    }
    AstNodeKinds kind = ast.kind;
    return kind == AstNodeKinds.NOT
        || kind == AstNodeKinds.NEG;
  }

  private boolean isBinOp(AbstractSyntaxTree ast) {
    if (ast == null) {
      return false;
    }
    AstNodeKinds kind = ast.kind;
    return kind == AstNodeKinds.EQEQ
        || kind == AstNodeKinds.NOTEQ
        || kind == AstNodeKinds.LT
        || kind == AstNodeKinds.LTEQ
        || kind == AstNodeKinds.GT
        || kind == AstNodeKinds.GTEQ
        || kind == AstNodeKinds.LOR
        || kind == AstNodeKinds.LAND
        || kind == AstNodeKinds.OR
        || kind == AstNodeKinds.AND
        || kind == AstNodeKinds.PLUS
        || kind == AstNodeKinds.MINUS
        || kind == AstNodeKinds.MUL
        || kind == AstNodeKinds.DIV
        || kind == AstNodeKinds.MOD;

  }
}
