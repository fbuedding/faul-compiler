package lexer;

public class Token {

  public TokenType kind;
  public String lexem;
  public int line;
  public int linePos;

  public Token(TokenType token, String lexem) {
    this.kind = token;
    this.lexem = lexem;
  }

  Token(TokenType token, String lexem, int line, int linePos) {
    this.kind = token;
    this.lexem = lexem;
    this.line = line;
    this.linePos = linePos;
  }

  public Token(TokenType token, char lexem, int line, int linePos) {
    this.kind = token;
    this.lexem = String.valueOf(lexem);
    this.line = line;
    this.linePos = linePos;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof Token))
      return false;
    Token t = (Token) obj;
    return t.lexem.equals(this.lexem) && t.kind == this.kind;
  }

  @Override
  public String toString() {
    if (!this.lexem.equals(""))
      return String.format("Kind: <%s%s%s>, Lexem: '%s%s%s'", ANSI_RED, this.kind, ANSI_RESET, ANSI_GREEN, this.lexem,
          ANSI_RESET);
    else
      return String.format("Kind: <%s%s%s>", ANSI_RED, this.kind, ANSI_RESET);
  }

  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_BLACK = "\u001B[30m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String ANSI_YELLOW = "\u001B[33m";
  public static final String ANSI_BLUE = "\u001B[34m";
  public static final String ANSI_PURPLE = "\u001B[35m";
  public static final String ANSI_CYAN = "\u001B[36m";
  public static final String ANSI_WHITE = "\u001B[37m";
}
