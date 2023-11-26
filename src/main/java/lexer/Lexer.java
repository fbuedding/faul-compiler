package lexer;

import fsm.*;

public class Lexer {

  public static final String WORD_START = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
  private char[] input;
  private char currentCharacter;

  private int currentPosition = -1;

  private int currentLine = 0;
  private int currentLinePosition = -1;
  private Fsm<Character> intFsm;
  private Fsm<Character> wordFsm;

  Lexer(String input) {
    this.input = (input + "\n").toCharArray();
    intFsm = Fsm.integerFsm();
    wordFsm = Fsm.wordFsm();
    nextCharacter();
  }

  public char getCurrentCharacter() {
    return currentCharacter;
  }

  public Token getToken() throws LexerError {
    Token t;
    // Single character operator
    if (currentCharacter == '+')
      t = new Token(TokenType.PLUS, currentCharacter);

    else if (currentCharacter == '-')
      t = new Token(TokenType.MINUS, currentCharacter);

    else if (currentCharacter == '*')
      t = new Token(TokenType.ASTERISK, currentCharacter);

    else if (currentCharacter == '/')
      t = new Token(TokenType.SLASH, currentCharacter);

    else if (currentCharacter == '(')
      t = new Token(TokenType.OPEN_BRACKET, currentCharacter);

    else if (currentCharacter == ')')
      t = new Token(TokenType.CLOSE_BRACKET, currentCharacter);

    // Multi character operator
    else if (currentCharacter == '=') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.EQEQ, "=" + currentCharacter);
        nextCharacter();
      } else
        t = new Token(TokenType.EQ, currentCharacter);
    }

    else if (currentCharacter == '!') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.NOTEQ, currentCharacter + "=");
        nextCharacter();
      } else
        t = new Token(TokenType.NOT, currentCharacter);
    }

    else if (currentCharacter == '<') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.LTEQ, currentCharacter + "=");
        nextCharacter();
      } else
        t = new Token(TokenType.LT, currentCharacter);
    }

    else if (currentCharacter == '>') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.GTEQ, currentCharacter + "=");
        nextCharacter();
      } else
        t = new Token(TokenType.GT, currentCharacter);
    }

    // Integer
    else if (isDigit(currentCharacter)) {
      // return early
      return matchInt();
    }
    // This is matching words, it's gonna be either a Keyword, literal bool
    else if (isWordStart(currentCharacter)) {
      // early return
      return matchWord();
    } else if (currentCharacter == ';') {
      t = new Token(TokenType.SEMICOLON, currentCharacter);
    } else if (currentCharacter == '{') {
      t = new Token(TokenType.OPEN_PARANTHESES, currentCharacter);
    } else if (currentCharacter == '}') {
      t = new Token(TokenType.CLOSE_PARANTHESES, currentCharacter);
    }
    // End of file
    else if (currentCharacter == '\0')
      t = new Token(TokenType.EOF, currentCharacter);
    else
      throw new LexerError("Unknown Token", currentLine, currentLinePosition);
    nextCharacter();
    return t;
  }

  public void nextCharacter() {
    currentPosition++;
    currentLinePosition++;
    if (currentPosition >= input.length)
      currentCharacter = '\0';
    else {
      currentCharacter = input[currentPosition];
      if (currentCharacter == '\n') {
        currentLine++;
        currentLinePosition = -1;
        nextCharacter();
      } else if (Character.isWhitespace(currentCharacter)) {
        nextCharacter();
      }
    }
  }

  public char lookAhead() {
    if (currentPosition + 1 < input.length) {
      return input[currentPosition + 1];
    } else
      return '\0';
  }

  private Token matchInt() throws LexerError {
    Token t = new Token(TokenType.V_INT, "");
    try {
      intFsm.nextState(currentCharacter);
      t.lexem = t.lexem.concat(String.valueOf(currentCharacter));
      while (true) {
        nextCharacter();
        intFsm.nextState(currentCharacter);
        if (intFsm.isEndstate())
          break;
        t.lexem = t.lexem.concat(String.valueOf(currentCharacter));
      }
    } catch (NoTransitionError e) {
      throw new LexerError("Invalid character in int: <" + String.valueOf(currentCharacter) + ">",
          currentLine,
          currentLinePosition);
    } finally {
      intFsm.reset();
    }

    return t;
  }

  private Token matchWord() throws LexerError {
    Token t = new Token(TokenType.IDENT, "");
    try {
      wordFsm.nextState(currentCharacter);
      t.lexem = t.lexem.concat(String.valueOf(currentCharacter));
      wordFsm.nextState(lookAhead());
      while (true) {
        nextCharacter();
        if (wordFsm.isEndstate())
          break;
        wordFsm.nextState(currentCharacter);
        t.lexem = t.lexem.concat(String.valueOf(currentCharacter));
      wordFsm.nextState(lookAhead());
      }
    } catch (NoTransitionError e) {
      throw new LexerError("Invalid character in identifier: <" + String.valueOf(currentCharacter), currentLine,
          currentLinePosition);
    } finally {
      wordFsm.reset();
    }
    if (t.lexem.equals("if")) {
      t.kind = TokenType.IF;
    } else if (t.lexem.equals("int")) {
      t.kind = TokenType.INT;
    } else if (t.lexem.equals("bool")) {
      t.kind = TokenType.BOOL;
    } else if (t.lexem.equals("true") || t.lexem.equals("false")) {
      t.kind = TokenType.V_BOOL;
    }
    return t;
  }

  private boolean isDigit(char currChar) {
    return Character.isDigit(currChar);
  }

  private boolean isWordStart(char currChar) {
    return WORD_START.contains(String.valueOf(currChar));
  }
}
