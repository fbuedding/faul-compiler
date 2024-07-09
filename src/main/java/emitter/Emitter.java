package emitter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import ast.AbstractSyntaxTree;
import ast.AstNodeKinds;
import ast.AstNodeTypes;
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

  private String initVar(int address) {
    // if it's there it's out of scope
    loaded.remove(address);
    String reg = "";
    if (!available.isEmpty()) {
      reg = available.pop();
    } else {
      reg = freeLoadedVar();
    }
    loaded.putFirst(address, reg);
    emit("# initializing %s with address %d", reg, address * wordLength);
    emit("move $%s, $0", reg);
    return reg;
  }

  private String loadVar(int address) {
    String reg = loaded.remove(address);
    if (reg == null && available.size() > 0) {
      reg = available.pop();
      emit("# loading %s from address %d", reg, address * wordLength);
      emitLw(reg, address);
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedVar();
    }
    loaded.putFirst(address, reg);

    return reg;

  }

  private void assignVar(int address, String thatReg) {
    String reg = loaded.remove(address);
    if (reg == null && !available.isEmpty()) {
      reg = available.pop();
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedVar();
    }
    loaded.putFirst(address, reg);

    emit("move $%s, $%s", reg, thatReg);

  }

  private String freeLoadedVar() {
    Entry<Integer, String> entry = loaded.pollLastEntry();
    String reg = entry.getValue();
    int address = entry.getKey();
    emit("# unloading %s and storing in address %d", reg, address * wordLength);
    emitSw(reg, address);
    return reg;

  }

  private void saveAllVars() {
    emit("# unloading all vars");
    while (!loaded.isEmpty()) {
      Entry<Integer, String> entry = loaded.pollLastEntry();
      String reg = entry.getValue();
      int address = entry.getKey();
      available.push(reg);
      emit("# unloading %s and storing in address %d", reg, address * wordLength);
      emitSw(reg, address);

    }
  }

  private void emit(String s) {
    code.append(s);
    code.append("\n");
  }

  private void emitExit() {
    emitComment("Exit");
    code.append("li $v0, 10\nsyscall");
    code.append("\n");
  }

  private void emitComment(String s, Object... args) {
    code.append("# " + String.format(s, args));
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
    initVar(address);

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
    saveAllVars();
    emitExit();
  }

  private void assignment(AbstractSyntaxTree ast, SymbolTable sTable) {
    String ident = ast.getChild(AstNodeKinds.IDENT).value;
    AbstractSyntaxTree secondChild = ast.getChild(1);
    int address = sTable.getAddress(ident);
    // TODO make this an error
    String reg = "MISSING REG";
    if (isBinOp(secondChild)) {
      reg = binOp(secondChild, sTable);
    } else if (isUnOp(secondChild)) {
      reg = unOp(secondChild, sTable);

    } else if (isVal(secondChild)) {
      reg = loadVar(address);
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
      // early return since var is already assigned
      return;
    }
    decrementRegCounter(reg);
    assignVar(address, reg);
    // emit("sw $%s, %d(%s)", reg, address, memOffsetReg);
  }

  /**
   * @param ast
   * @param sTable
   * @return register with the result in it
   */
  private String binOp(AbstractSyntaxTree ast, SymbolTable sTable) {
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
      case MOD:
        emit("# Modulo");
        emit("div $%s, $%s", leftReg, rightReg);
        emit("mfhi $%s", tempReg);
        break;
      case EQEQ:
        emit("# Equals");
        emit("seq $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case NOTEQ:
        emit("# Not equals");
        emit("sne $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case GT:
        emit("# Greater than");
        emit("sgt $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case GTEQ:
        emit("# Greater than equal");
        emit("sge $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case LT:
        emit("# Less than");
        emit("slt $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case LTEQ:
        emit("# Less than equals");
        emit("sle $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;

      // Das ergebnis ist dasselbe, daher zusammen gefasst 
      case LOR, OR:
        emit("or $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case LAND, AND:
        emit("and $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;

      default:
        break;
    }
    return tempReg;
  }

  private String unOp(AbstractSyntaxTree ast, SymbolTable sTable) {
    String reg = handleChild(ast.getChild(0), sTable);
    decrementRegCounter(reg);
    String tempReg = getReg(Register.T);
    switch (ast.getKind()) {
      case NOT:
        emit("nor $%s, $%s, $%<s", tempReg, reg);
        if (ast.resultType == AstNodeTypes.BOOLEAN) {
          emit("andi $%s, $%<s, 1", tempReg);
        }
        break;
      case NEG:
        emitComment("negating $s", reg);
        emit("sub $%s, $0, $%s", tempReg, reg);

        break;

      default:
        break;
    }
    return tempReg;
  }

  private String id(AbstractSyntaxTree ast, SymbolTable sTable) {
    int address = sTable.getAddress(ast.value);
    String reg = loadVar(address);
    return reg;
  }

  private String handleChild(AbstractSyntaxTree ast, SymbolTable sTable) {
    // TODO make this an error, but only when everything is implemented
    String reg = "MISSING REG";
    if (isBinOp(ast)) {
      reg = binOp(ast, sTable);
    } else if (isUnOp(ast)) {
      reg = unOp(ast, sTable);
    } else if (isId(ast)) {
      reg = id(ast, sTable);
    } else if (isVal(ast)) {
      reg = val(ast, sTable);
    }
    return reg;
  }

  private String val(AbstractSyntaxTree ast, SymbolTable sTable) {
    String reg = getReg(Register.V);
    String val = ast.value;
    if (val.equals("true")) {
      val = "1";
    } else if (val.equals("false")) {
      val = "0";
    }
    emitComment("Loading value %s", val);
    emit("li $%s, %s", reg, val);
    return reg;

  }

  private void decrementRegCounter(String... regs) {
    for (String reg : regs) {
      if (reg.startsWith("t")) {
        tempCount--;
      } else if (reg.startsWith("s")) {
        // save register werden nur unloaded wenn platz benötigt wird
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
        throw new Error("Saved registers are LRU cached organized");

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
