package types;

import java.util.Arrays;

/**
 * Type
 */
public class Type {
  public static Type tVoid() {
    Type t = new Type();
    t.setType(Types.VOID);
    t.retType = null;
    return t;
  }

  private Types type;

  private Types retType;

  public Type(Types type) {
    this.type = type;
    this.retType = Types.VOID;
  }

  public Type(Types type, Types retType) {
    this.type = type;
    this.retType = retType;
  }

  public Type(Types type, Type retType) {
    this.type = type;
    this.retType = retType.type;
  }

  private Type() {
  }

  public Types getType() {
    return type;
  }

  @Override
  public String toString() {
    return String.format("T: %s; rT: %s", type, retType);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {
        type, // auto-boxed
        retType, // auto-boxed

    });
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Type)) {
      return false;
    }
    Type that = (Type) obj;
    return this.type == that.type;
  }

  public void inferType(Type that) {
    this.type = that.retType;
  }

  /**
   * @param that the type to check
   * @return true if {@link Type}s return type is euqal
   */
  public boolean checkType(Type that) {

    return this.type == that.retType;
  }

  public boolean isUnknown() {
    return type == Types.UNKNOWN;
  }

  public void setType(Types type) {
    this.type = type;
  }

  public Types getRetType() {
    return retType;
  }

  public void setRetType(Type retType) {
    this.retType = retType.type;
  }

  public void setRetType(Types retType) {
    this.retType = retType;
  }

  public boolean checkType(Types that) {
    return this.type == that;
  }

  public boolean isRetUnknown() {
    return retType == Types.UNKNOWN;
  }

}
