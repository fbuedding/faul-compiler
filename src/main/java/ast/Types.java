package ast;

public enum Types {
  INTEGER,
  BOOLEAN,
  MEMORY_ADDRESS,
  VOID,
  UNKNOWN,
  FUNCTION;

  private Types subType;

  public Types getSubType() {
    return subType;
  }

  public Types setSubType(Types subType) {
    this.subType = subType;
    return this;
  }

  public void setSubTypeNone() {
    this.subType = VOID;
  }

  Types() {
    setSubTypeNone();
  }

  Types(Types subType) {
    this.subType = subType;
  }
}
