package lexer;

public class Token {

  public TokenType kind;
  public String lexem;

  Token(TokenType token, String lexem) {
    this.kind = token;
    this.lexem = lexem;
  }

  public Token(TokenType token, char lexem) {
    this.kind = token;
    this.lexem = String.valueOf(lexem);
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) return true;
    if(!(obj instanceof Token)) return false;
    Token t = (Token) obj;
    return t.lexem.equals(this.lexem) && t.kind == this.kind;
  }
  @Override
  public String toString() {
    return "Token {Kind: " + this.kind + ", Lexem: " + this.lexem+"}";
  }
}
