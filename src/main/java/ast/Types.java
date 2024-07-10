package ast;

public enum Types {
  INTEGER,
  BOOLEAN,
  MEMORY_ADDRESS,
  NONE,
  UNKNOWN,
  FUNCTION;

  private Types subType;

  public Types getSubType() {
    return subType;
  }

  public void setSubType(Types subType) {
    this.subType = subType;
  }

  public void setSubTypeNone() {
    this.subType = NONE;
  }

  Types() {
    setSubTypeNone();
  }

  Types(Types subType) {
    this.subType = subType;
  }
}
