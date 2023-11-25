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
}
