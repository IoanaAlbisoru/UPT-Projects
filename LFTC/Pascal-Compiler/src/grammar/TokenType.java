package grammar;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum TokenType {
  ERROR,
    COMMENT,
    EOF,
    IDENTIFIER,

    NUMBER_INTEGER,
    NUMBER_BASE,
    NUMBER_REAL,

    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    ROUND_BRACKET_OPEN,
    ROUND_BRACKET_CLOSE,
    SQUARE_BRACKET_OPEN,
    SQUARE_BRACKET_CLOSE,
    LESS,
    LESS_OR_EQUAL,
    EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    DIFFERENT,
    ASSIGN,

    SEMICOLON,
    COLON,
    DOT,
    DOUBLEDOT,
    COMMA,

    CHAR_CONST,
    STRING_CONST,

    AND_KW,
    ARRAY_KW,
    BEGIN_KW,
    CASE_KW,
    CHAR_KW,
    CONST_KW,
    DIV_KW,
    DO_KW,
    DOWNTO_KW,
    ELSE_KW,
    END_KW,
    FOR_KW,
    FUNCTION_KW,
    IF_KW,
    INTEGER_KW,
    MOD_KW,
    NOT_KW,
    OF_KW,
    OR_KW,
    PROCEDURE_KW,
    PROGRAM_KW,
    REAL_KW,
    REPEAT_KW,
    RECORD_KW,
    THEN_KW,
    TO_KW,
    UNTIL_KW,
    VAR_KW,
    WHILE_KW,
    PRINT_KW,
    READ_KW;

  // TODO not used yet
  public static final Set<TokenType> OPERATOR_SET;
  static {
    Set<TokenType> tempSet = EnumSet.range(PLUS, ASSIGN);
    tempSet.add(DIV_KW);
    OPERATOR_SET = Collections.unmodifiableSet(tempSet);
  }

  // TODO not used yet
  public static final Set<TokenType> KEYWORD_SET;
  static {
    Set<TokenType> tempSet = EnumSet.range(AND_KW, WHILE_KW);
    tempSet.add(DIV_KW);
    KEYWORD_SET = Collections.unmodifiableSet(tempSet);
  }

  @SuppressWarnings("serial")
  public static final Map<String, TokenType> KEYWORD_TO_TOKEN_MAP = Collections
      .unmodifiableMap(new HashMap<String, TokenType>() {
        {
          this.put("and", TokenType.AND_KW);
          this.put("array", TokenType.ARRAY_KW);
          this.put("begin", TokenType.BEGIN_KW);
          this.put("case", TokenType.CASE_KW);
          this.put("char", TokenType.CHAR_KW);
          this.put("const", TokenType.CONST_KW);
          this.put("div", TokenType.DIV_KW);
          this.put("do", TokenType.DO_KW);
          this.put("downto", TokenType.DOWNTO_KW);
          this.put("else", TokenType.ELSE_KW);
          this.put("end", TokenType.END_KW);
          this.put("for", TokenType.FOR_KW);
          this.put("function", TokenType.FUNCTION_KW);
          this.put("if", TokenType.IF_KW);
          this.put("integer", TokenType.INTEGER_KW);
          this.put("mod", TokenType.MOD_KW);
          this.put("not", TokenType.NOT_KW);
          this.put("of", TokenType.OF_KW);
          this.put("or", TokenType.OR_KW);
          this.put("procedure", TokenType.PROCEDURE_KW);
          this.put("program", TokenType.PROGRAM_KW);
          this.put("real", TokenType.REAL_KW);
          this.put("repeat", TokenType.REPEAT_KW);
          this.put("record", TokenType.RECORD_KW);
          this.put("then", TokenType.THEN_KW);
          this.put("to", TokenType.TO_KW);
          this.put("until", TokenType.UNTIL_KW);
          this.put("var", TokenType.VAR_KW);
          this.put("while", TokenType.WHILE_KW);
          this.put("print", TokenType.PRINT_KW);
          this.put("read", TokenType.READ_KW);
        }
      });

}
