package lexer;

import java.util.Arrays;
import java.util.Vector;

import fsm.*;

public class Lexer {

  public static final String WORD_START = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
  private char[] input;
  private char currentCharacter;

  private int currentPosition = -1;

  private int currentLine = 1;
  private int currentLinePosition = 0;
  private Fsm<Character> intFsm;
  private Fsm<Character> wordFsm;

  public Lexer(String input) {
    this.input = (input + "\n").toCharArray();
    intFsm = Fsm.integerFsm();
    wordFsm = Fsm.wordFsm();
    nextCharacter();
  }

  public char getCurrentCharacter() {
    return currentCharacter;
  }

  public Token[] genTokens() throws LexerError {
    Vector<Token> tokens = new Vector<Token>();
    Token token = getToken();
    while (token.kind != TokenKind.EOF) {
      tokens.add(token);
      token = getToken();
    }
    tokens.add(token);
    return Arrays.copyOf(tokens.toArray(), tokens.size(), Token[].class);
  };

  public Token getToken() throws LexerError {
    Token t;
    if (currentCharacter == '+')
      t = new Token(TokenKind.PLUS, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == '-')
      t = new Token(TokenKind.MINUS, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == '*')
      t = new Token(TokenKind.ASTERISK, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == '/')
      t = new Token(TokenKind.SLASH, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == '%')
      t = new Token(TokenKind.PERCENT, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == '(')
      t = new Token(TokenKind.OPEN_BRACKET, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == ')')
      t = new Token(TokenKind.CLOSE_BRACKET, currentCharacter, currentLine, currentLinePosition);

    else if (currentCharacter == ',')
      t = new Token(TokenKind.COMMA, currentCharacter, currentLine, currentLinePosition);


    // Multi character operator
    else if (currentCharacter == '=') {
      if (lookAhead() == '=') {
        t = new Token(TokenKind.EQEQ, "=" + currentCharacter, currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.EQ, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '!') {
      if (lookAhead() == '=') {
        t = new Token(TokenKind.NOTEQ, currentCharacter + "=", currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.NOT, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '<') {
      if (lookAhead() == '=') {
        t = new Token(TokenKind.LTEQ, currentCharacter + "=", currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.LT, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '>') {
      if (lookAhead() == '=') {
        t = new Token(TokenKind.GTEQ, currentCharacter + "=", currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.GT, currentCharacter, currentLine, currentLinePosition);
    } else if (currentCharacter == '|') {
      if (lookAhead() == '|') {
        t = new Token(TokenKind.LOR, currentCharacter + "|", currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.OR, currentCharacter, currentLine, currentLinePosition);
    } else if (currentCharacter == '&') {
      if (lookAhead() == '&') {
        t = new Token(TokenKind.LAND, currentCharacter + "&", currentLine, currentLinePosition);
        nextCharacter();
      } else
        t = new Token(TokenKind.AND, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == ';') {
      t = new Token(TokenKind.SEMICOLON, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '{') {
      t = new Token(TokenKind.OPEN_PARANTHESES, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '}') {
      t = new Token(TokenKind.CLOSE_PARANTHESES, currentCharacter, currentLine, currentLinePosition);
    }

    else if (currentCharacter == '\0')
      t = new Token(TokenKind.EOF, currentCharacter, currentLine, currentLinePosition);
    // Integer and word match returns early, because

    else if (currentCharacter == '0') {
      if (isDigit(lookAhead())) {
        throw new LexerError("Numbers cannot start with 0", currentLine, currentPosition);
      }
      t = new Token(TokenKind.V_INT, currentCharacter, currentLine, currentLinePosition);
    }

    else if (isDigit(currentCharacter)) {
      return matchInt();
    }

    else if (isWordStart(currentCharacter)) {
      return matchWord();
    } else {
      throw new LexerError("Unknown Token", currentLine, currentLinePosition);
    }
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
        currentLinePosition = 0;
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
    Token t = new Token(TokenKind.V_INT, "", currentLine, currentLinePosition);
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
    Token t = new Token(TokenKind.IDENT, "", currentLine, currentLinePosition);
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
      throw new LexerError("Invalid character in identifier: <" + String.valueOf(currentCharacter) + ">", currentLine,
          currentLinePosition);
    } finally {
      wordFsm.reset();
    }
    if (t.lexem.equals("if")) {
      t.kind = TokenKind.IF;
    } else if (t.lexem.equals("else")) {
      t.kind = TokenKind.ELSE;
    } else if (t.lexem.equals("while")) {
      t.kind = TokenKind.WHILE;
    } else if (t.lexem.equals("int")) {
      t.kind = TokenKind.INT;
    } else if (t.lexem.equals("bool")) {
      t.kind = TokenKind.BOOL;
    } else if (t.lexem.equals("true") || t.lexem.equals("false")) {
      t.kind = TokenKind.V_BOOL;
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
