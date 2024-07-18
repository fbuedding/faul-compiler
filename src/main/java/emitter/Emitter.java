package emitter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import ast.AbstractSyntaxTree;
import ast.AstNodeKinds;
import ast.SymbolTable;
import error.CompileError;
import error.UnexpectedError;
import types.Types;

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

    Register(String label) {
      this.label = label;
    }
  }

  StringBuilder code;

  public StringBuilder getCode() {
    return code;
  }

  int tempCount = 0;
  int valCount = 0;
  int labelCount = 0;
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
    code = new StringBuilder();
    if (has$Gp) {
      memOffsetReg = "$gp";
    } else {

      memOffsetReg = "$0";

    }
  }

  public void generate() throws CompileError {

    generate(ast, sTable);
  }

  private void emitNop() {

    emit("nop");
  }

  private String declareVar(int address) {
    // if it's there it's out of scope
    loaded.remove(address);
    String reg = "";
    if (!available.isEmpty()) {
      reg = available.pop();
    } else {
      reg = freeLoadedVar();
    }
    loaded.putFirst(address, reg);
    emitComment("initializing %s with address %d", reg, address * wordLength);
    emitMv(reg, "0");
    return reg;
  }

  private String loadVar(int address) {
    String reg = loaded.remove(address);
    if (reg == null && available.size() > 0) {
      reg = available.pop();
      emitComment("loading %s from address %d", reg, address * wordLength);
      emitLw(reg, address);
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedVar();
      emitLw(reg, address);
    }
    loaded.putFirst(address, reg);

    return reg;

  }

  private void assignVar(int address, String thatReg) {
    String reg = loaded.remove(address);
    emitComment("Assgining reg %s with address %s", reg, address);
    if (reg == null && !available.isEmpty()) {
      reg = available.pop();
    } else if (reg == null && available.size() == 0) {
      reg = freeLoadedVar();
      emitLw(reg, address);
    }
    loaded.putFirst(address, reg);

    emitMv(reg, thatReg);
  }

  private String freeLoadedVar() {
    Entry<Integer, String> entry = loaded.pollLastEntry();
    String reg = entry.getValue();
    int address = entry.getKey();
    emitComment("unloading %s and storing in address %d", reg, address * wordLength);
    emitSw(reg, address);
    return reg;

  }

  private void saveAllVars() {
    emitComment("unloading all vars");
    while (!loaded.isEmpty()) {
      Entry<Integer, String> entry = loaded.pollLastEntry();
      String reg = entry.getValue();
      int address = entry.getKey();
      available.push(reg);
      emitComment("unloading %s and storing in address %d", reg, address * wordLength);
      emitSw(reg, address);

    }
  }

  private void emitTab() {
    code.append("\t");
    code.append("\t");
  }

  private void emit(String s) {
    emitTab();
    code.append(s);
    code.append("\n");
  }

  private void emitLabel(String label) {
    code.append(String.format("%s:", label));
    code.append("\n");
  }

  private void emitExit() {
    emitComment("Exit");

    emit("li $v0, 10");

    emit("syscall");
  }

  private void emitComment(String s, Object... args) {
    code.append("# " + String.format(s, args));
    code.append("\n");
  }

  private void emit(String s, Object... args) {
    emitTab();
    code.append(String.format(s, args));
    code.append("\n");
  }

  private void emitMv(String reg, String reg2) {

    emit("move $%s, $%s", reg, reg2);
  }

  private void emitStackPush(String reg) {

    emit("addi $sp, $sp, -4");

    emit("sw $%s, 0($sp)", reg);

  }

  private void emitStackPop(String reg) {

    emit("lw $%s, 0($sp)", reg);

    emit("addi $sp, $sp, 4");
  }

  private void emitLw(String reg, int address) {

    emit("lw $%s, %d(%s)", reg, address * wordLength, memOffsetReg);
  }

  private void emitSw(String reg, int address) {

    emit("sw $%s, %d(%s)", reg, address * wordLength, memOffsetReg);
  }

  private void emitPrint() {
    emitLabel("print");
    emitStackPush("ra");
    emit("li $v0, 1");
    emit("syscall");
    emitNop();
    emitStackPop("ra");

    emit("jr $ra");
    emitNop();
  }

  private void emitReadI() {
    emitLabel("readI");
    emitStackPush("ra");
    emit("li $v0, 5");
    emit("syscall");
    emitNop();
    emitStackPop("ra");
    emit("jr $ra");
    emitNop();
  }

  private void emitReadB() {
    emitLabel("readB");
    emitStackPush("ra");
    emit("li $v0, 5");
    emit("syscall");
    emitNop();
    emit("andi $v0, $v0, 1");
    emitStackPop("ra");
    emit("jr $ra");
    emitNop();
  }

  private void emitInitVar(AbstractSyntaxTree ast) {
    int address = Integer.parseInt(ast.value);
    // loadWordIntoReg(address);
    // emit("sw $0, %d(%s)", address, memOffsetReg);
    declareVar(address);

  }

  private void generate(AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    branch(ast, sTable, "main");
    saveAllVars();
    emitExit();

    emitReadI();
    emitReadB();
    emitPrint();
    emitLabel("exit");
    emitExit();
  }

  private void branch(AbstractSyntaxTree ast, SymbolTable sTable, String label) throws CompileError {
    emitLabel(label);
    branch(ast, sTable);
  }

  private void branch(AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    // saveAllVars();
    for (AbstractSyntaxTree currentAst : ast.children) {
      switch (currentAst.kind) {
        case DECLARATION:
          emitInitVar(currentAst.getChild(AstNodeKinds.ADDRESS));
          break;
        case ASSIGNMENT:
          assignment(currentAst, sTable);
          break;
        case IF:
          ifNode(currentAst, sTable);
          break;
        case WHILE:
          whileNode(currentAst, sTable);

          break;
        case FUNC_CALL:
          funcCall(currentAst, sTable);
          break;
        default:
          throw new UnexpectedError("Unknown or unimplemented statement kind " + currentAst.kind, currentAst.line,
              currentAst.linePos);
      }
    }
    // saveAllVars();
  }

  private String funcCall(AbstractSyntaxTree ast, SymbolTable sTable) throws UnexpectedError {
    String ident = ast.getChild(AstNodeKinds.IDENT).value;
    String label = sTable.getLabel(ident);
    if (label == null) {
      throw new UnexpectedError("Function undefined", ast.line, ast.linePos);
    }
    String[] paramRegs = { "a0", "a1", "a2", "a3" };
    for (int i = 1; i < ast.getChildrenCount(); i++) {
      String reg = expression(ast.getChild(i).getChild(0), sTable);
      decrementRegCounter(reg);
      try {
        emitMv(paramRegs[i - 1], reg);
      } catch (IndexOutOfBoundsException e) {
        throw new UnexpectedError("Function regs out of bounce (only parameters are possible)", ast.line, ast.linePos);
      }
    }
    emit("jal %s", label);
    emitNop();
    return "v0";
  }

  private void whileNode(AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    if (ast.getChildrenCount() != 2) {
      throw new UnexpectedError("While is empty", ast.line, ast.linePos);
    }
    String endLabel = getLabel();
    AbstractSyntaxTree condition = ast.getChild(AstNodeKinds.CONDITION);
    if (condition == null || condition.getChildrenCount() == 0) {
      throw new UnexpectedError("While-Condition is empty", ast.line, ast.linePos);
    }
    emitComment("While start");
    String loopLabel = getLabel();
    emitLabel(loopLabel);
    String conditionReg = expression(condition.getChild(0), sTable);
    emit("beqz $%s, %s", conditionReg, endLabel);
    emitNop();
    branch(ast.getChild(1), sTable.popScopedSymbolTable());
    emit("b %s", loopLabel);
    emitNop();
    emitComment("While end");
    emitLabel(endLabel);
  }

  private void ifNode(AbstractSyntaxTree ast, SymbolTable sTable) throws CompileError {
    String endLabel = getLabel();
    AbstractSyntaxTree condition = ast.getChild(AstNodeKinds.CONDITION);
    if (condition == null) {
      throw new UnexpectedError("If-Condition is empty", ast.line, ast.linePos);
    }

    emitComment("If start");
    String conditionReg = expression(condition.getChild(0), sTable);
    switch (ast.getChildrenCount()) {
      case 2:
        // no else branch
        emit("beqz $%s, %s", conditionReg, endLabel);
        emitNop();
        branch(ast.getChild(1), sTable.popScopedSymbolTable());
        break;
      case 3:
        String elseLabel = getLabel();
        emit("beqz $%s, %s", conditionReg, elseLabel);
        emitNop();
        branch(ast.getChild(1), sTable.popScopedSymbolTable());
        emit("b %s", endLabel);
        emitNop();
        emitComment("else start");
        emitLabel(elseLabel);
        branch(ast.getChild(2), sTable.popScopedSymbolTable());
        break;
      default:
    }
    emitComment("If end");
    emitLabel(endLabel);
  }

  private String getLabel() {
    return String.format("label_%d", labelCount++);
  }

  private void assignment(AbstractSyntaxTree ast, SymbolTable sTable) throws UnexpectedError {
    String ident = ast.getChild(AstNodeKinds.IDENT).value;
    AbstractSyntaxTree secondChild = ast.getChild(1);
    int address = sTable.getAddress(ident);
    String reg;
    // Special case, if the second child is a value we don't need a temporary reg
    if (isVal(secondChild)) {
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
    reg = expression(secondChild, sTable);
    decrementRegCounter(reg);
    assignVar(address, reg);
    // emit("sw $%s, %d(%s)", reg, address, memOffsetReg);
  }

  /**
   * @param ast
   * @param sTable
   * @return register with the result in it
   * @throws UnexpectedError
   */
  private String binOp(AbstractSyntaxTree ast, SymbolTable sTable) throws UnexpectedError {
    AbstractSyntaxTree leftChild = ast.getChild(0);
    AbstractSyntaxTree rightChild = ast.getChild(1);
    String leftReg = "", rightReg = "";
    if (leftChild.depth() >= rightChild.depth()) {
      leftReg = expression(leftChild, sTable);
      rightReg = expression(rightChild, sTable);
    } else {
      rightReg = expression(rightChild, sTable);
      leftReg = expression(leftChild, sTable);
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
        emitComment("Modulo");
        emit("div $%s, $%s", leftReg, rightReg);
        emit("mfhi $%s", tempReg);
        break;
      case EQEQ:
        emitComment("Equals");
        emit("seq $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case NOTEQ:
        emitComment("Not equals");
        emit("sne $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case GT:
        emitComment("Greater than");
        emit("sgt $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case GTEQ:
        emitComment("Greater than equal");
        emit("sge $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case LT:
        emitComment("Less than");
        emit("slt $%s, $%s, $%s", tempReg, leftReg, rightReg);
        break;
      case LTEQ:
        emitComment("Less than equals");
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

  private String unOp(AbstractSyntaxTree ast, SymbolTable sTable) throws UnexpectedError {
    String reg = expression(ast.getChild(0), sTable);
    decrementRegCounter(reg);
    String tempReg = getReg(Register.T);
    switch (ast.getKind()) {
      case NOT:
        emit("nor $%s, $%s, $%<s", tempReg, reg);
        if (ast.type.checkType(Types.BOOLEAN)) {
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

  private String expression(AbstractSyntaxTree ast, SymbolTable sTable) throws UnexpectedError {
    String reg;
    if (isBinOp(ast)) {
      reg = binOp(ast, sTable);
    } else if (isUnOp(ast)) {
      reg = unOp(ast, sTable);
    } else if (isId(ast)) {
      reg = id(ast, sTable);
    } else if (isVal(ast)) {
      reg = val(ast, sTable);
    } else if (isFunc(ast)) {
      reg = funcCall(ast, sTable);
    } else {
      throw new UnexpectedError("Unknown operator: " + ast.value, ast.line, ast.linePos);
    }
    return reg;
  }

  private boolean isFunc(AbstractSyntaxTree ast) {
    return ast.kind == AstNodeKinds.FUNC_CALL;
  }

  private String val(AbstractSyntaxTree ast, SymbolTable sTable) {
    String reg = getReg(Register.T);
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
