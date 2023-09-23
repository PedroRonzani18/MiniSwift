package lexical;

import interpreter.value.Value;

public class Token {

    public static enum Type {
        // Specials.
        INVALID_TOKEN,
        UNEXPECTED_EOF,
        END_OF_FILE,

        // Symbols.
        DOT,           // .
        COLON,         // :
        SEMICOLON,     // ;
        COMMA,         // ,
        OPEN_PAR,      // (
        CLOSE_PAR,     // )
        OPEN_BRA,      // [
        CLOSE_BRA,     // ]
        OPEN_CUR,      // {
        CLOSE_CUR,     // }

        // Operators.
        ASSIGN,        // =
        TERNARY,       // ?
        AND,           // &&
        OR,            // ||
        LOWER_THAN,    // <
        GREATER_THAN,  // >
        LOWER_EQUAL,   // <=
        GREATER_EQUAL, // >=
        EQUALS,        // ==
        NOT_EQUALS,    // !=
        ADD,           // +
        SUB,           // -
        MUL,           // *
        DIV,           // /
        NOT,           // !

        // Keywords.
        VAR,           // var
        LET,           // let
        PRINT,         // print
        PRINTLN,       // println
        DUMP,          // dump
        IF,            // if
        ELSE,          // else
        WHILE,         // while
        FOR,           // for
        IN,            // in
        BOOL,          // Bool
        INT,           // Int
        FLOAT,         // Float
        CHAR,          // Char
        STRING,        // String
        ARRAY,         // Array
        DICT,          // Dict
        FALSE,         // false
        TRUE,          // true
        READ,          // read
        RANDOM,        // random
        TO_BOOL,       // toBool
        TO_INT,        // toInt
        TO_FLOAT,      // toFloat
        TO_CHAR,       // toChar
        TO_STRING,     // toString 
        COUNT,         // count
        EMPTY,         // empty
        KEYS,          // keys
        VALUES,        // values
        APPEND,        // append
        CONTAINS,      // contains

        // Others.
        NAME,              // identifier
        INTEGER_LITERAL,   // integer literal
        FLOAT_LITERAL,     // float literal
        CHAR_LITERAL,      // char literal
        STRING_LITERAL     // string literal

    };

    public String lexeme;
    public Type type;
    public int line;
    public Value literal;

    public Token(String lexeme, Type type, Value literal) {
        this.lexeme = lexeme;
        this.type = type;
        this.line = 0;
        this.literal = literal;
    }

    public String toString() {
        return new StringBuffer()
            .append("(\"")
            .append(this.lexeme)
            .append("\", ")
            .append(this.type)
            .append(", ")
            .append(this.line)
            .append(", ")
            .append(this.literal)
            .append(")")
            .toString();
    }

}
