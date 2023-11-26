package lexer;

import fsm.*;

public class Lexer {

  private char[] input;
  private char currentCharacter;

  private int currentPosition = -1;

  private int currentLine = 0;
  private int currentLinePosition = -1;
  private Fsm<Character> intFsm;

  Lexer(String input) {
    this.input = (input + "\n").toCharArray();
    intFsm = Fsm.integerFsm();
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
        t = new Token(TokenType.EQEQ, currentCharacter);
        nextCharacter();
      } else
        t = new Token(TokenType.EQ, currentCharacter);
    }

    else if (currentCharacter == '!') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.NOTEQ, currentCharacter);
        nextCharacter();
      } else
        t = new Token(TokenType.NOT, currentCharacter);
    }

    else if (currentCharacter == '<') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.LTEQ, currentCharacter);
        nextCharacter();
      } else
        t = new Token(TokenType.LT, currentCharacter);
    }

    else if (currentCharacter == '>') {
      if (lookAhead() == '=') {
        t = new Token(TokenType.GTEQ, currentCharacter);
        nextCharacter();
      } else
        t = new Token(TokenType.GT, currentCharacter);
    }

    // Integer
    else if (isDigit(currentCharacter)) {
      t = matchInt();
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
      }
    }
  }

  public char lookAhead() {
    if (currentPosition + 1 < input.length)
      return input[currentPosition + 1];
    else
      return '\0';
  }

  private Token matchInt() throws LexerError {
    Token t = new Token(TokenType.V_INT, "");
    try {
      t.lexem = t.lexem.concat(String.valueOf(currentCharacter));

      while (intFsm.isNotEndstate()) {
        nextCharacter();
        intFsm.nextState(lookAhead());
        t.lexem = t.lexem.concat(String.valueOf(currentCharacter));
      }

    } catch (NoTransitionError e) {
      throw new LexerError("Invalid character in int: " + String.valueOf(currentCharacter), 
          currentLine,
          currentLinePosition);
    }finally {
      intFsm.reset();
    }

    return t;
  }

  private boolean isDigit(char currChar) {
    return Character.isDigit(currChar);
  }
}
