package lexer;

public class Token {

  public TokenKind kind;
  public String lexem;
  public int line;
  public int linePos;

  public Token(Token t, TokenKind kind) {
    this(kind, "", t.line, t.linePos);
  }


  public Token(TokenKind token, int line, int linePos) {
    this.kind = token;
    this.lexem = "";
    this.line = line;
    this.linePos = linePos;
  }
  public Token(TokenKind token, String lexem, int line, int linePos) {
    this.kind = token;
    this.lexem = lexem;
    this.line = line;
    this.linePos = linePos;
  }

  public Token(TokenKind token, char lexem, int line, int linePos) {
    this.kind = token;
    this.lexem = String.valueOf(lexem);
    this.line = line;
    this.linePos = linePos;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof Token t))
      return false;
    return t.lexem.equals(this.lexem) && t.kind == this.kind;
  }

  @Override
  public String toString() {
    if (!this.lexem.equals(""))
      return String.format("(Kind: <%s%s%s>; Lexem: '%s%s%s')", ANSI_RED, this.kind, ANSI_RESET, ANSI_GREEN, this.lexem,
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
