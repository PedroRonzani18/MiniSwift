package lexical;

import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.HashMap;
import java.util.Map;

import error.InternalException;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class LexicalAnalysis implements AutoCloseable {

    private int line;
    private PushbackInputStream input;
    private static Map<String, Token.Type> keywords;

    static {
        keywords = new HashMap<String, Token.Type>();

        // SYMBOLS
        keywords.put(".", Token.Type.DOT);
        keywords.put(":", Token.Type.COLON);
        keywords.put(";", Token.Type.SEMICOLON);
        keywords.put(",", Token.Type.COMMA);
        keywords.put("(", Token.Type.OPEN_PAR);
        keywords.put(")", Token.Type.CLOSE_PAR);
        keywords.put("[", Token.Type.OPEN_BRA);
        keywords.put("]", Token.Type.CLOSE_BRA);
        keywords.put("{", Token.Type.OPEN_CUR);
        keywords.put("}", Token.Type.CLOSE_CUR);

        // OPERATORS
        keywords.put("=", Token.Type.ASSIGN);
        keywords.put("?", Token.Type.TERNARY);
        keywords.put("&&", Token.Type.AND);
        keywords.put("||", Token.Type.OR);
        keywords.put("<", Token.Type.LOWER_THAN);
        keywords.put(">", Token.Type.GREATER_THAN);
        keywords.put("<=", Token.Type.LOWER_EQUAL);
        keywords.put(">=", Token.Type.GREATER_EQUAL);
        keywords.put("==", Token.Type.EQUALS);
        keywords.put("!=", Token.Type.NOT_EQUALS);
        keywords.put("+", Token.Type.ADD);
        keywords.put("-", Token.Type.SUB);
        keywords.put("*", Token.Type.MUL);
        keywords.put("/", Token.Type.DIV);
        keywords.put("!", Token.Type.NOT);

        // KEYWORDS
        keywords.put("var", Token.Type.VAR);
        keywords.put("let", Token.Type.LET);
        keywords.put("print", Token.Type.PRINT);
        keywords.put("println", Token.Type.PRINTLN);
        keywords.put("dump", Token.Type.DUMP);
        keywords.put("if", Token.Type.IF);
        keywords.put("else", Token.Type.ELSE);
        keywords.put("while", Token.Type.WHILE);
        keywords.put("for", Token.Type.FOR);
        keywords.put("in", Token.Type.IN);
        keywords.put("Bool", Token.Type.BOOL);
        keywords.put("Int", Token.Type.INT);
        keywords.put("Float", Token.Type.FLOAT);
        keywords.put("Char", Token.Type.CHAR);
        keywords.put("String", Token.Type.STRING);
        keywords.put("Array", Token.Type.ARRAY);
        keywords.put("Dict", Token.Type.DICT);
        keywords.put("false", Token.Type.FALSE);
        keywords.put("true", Token.Type.TRUE);
        keywords.put("read", Token.Type.READ);
        keywords.put("random", Token.Type.RANDOM);
        keywords.put("toBool", Token.Type.TO_BOOL);
        keywords.put("toInt", Token.Type.TO_INT);
        keywords.put("toFloat", Token.Type.TO_FLOAT);
        keywords.put("toChar", Token.Type.TO_CHAR);
        keywords.put("toString", Token.Type.TO_STRING);
        keywords.put("count", Token.Type.COUNT);
        keywords.put("empty", Token.Type.EMPTY);
        keywords.put("keys", Token.Type.KEYS);
        keywords.put("values", Token.Type.VALUES);
        keywords.put("append", Token.Type.APPEND);
        keywords.put("contains", Token.Type.CONTAINS);
    }

    public LexicalAnalysis(InputStream is) {
        input = new PushbackInputStream(is);
        line = 1;
    }

    public void close() {
        try {
            input.close();
        } catch (Exception e) {
            throw new InternalException("Unable to close file");
        }
    }

    public int getLine() {
        return this.line;
    }

    public Token nextToken() {
        Token token = new Token("", Token.Type.END_OF_FILE, null);

        int state = 1;
        while (state != 14 && state != 15) {
            int c = getc();
            // System.out.printf(" [%02d, %03d ('%c')]\n",
            // state, c, (char) c);

            switch (state) {
                case 1:
                    if (c == ' ' || c == '\t' || c == '\r') {
                        state = 1;
                    } else if (c == '\n') {
                        state = 1;
                        line++;
                    } else if (c == '/') {
                        state = 2;
                    } else if (c == '=' || c == '!' || c == '<' || c == '>') {
                        token.lexeme += (char) c;
                        state = 5;
                    } else if (c == '&') {
                        token.lexeme += (char) c;
                        state = 6;
                    } else if (c == '|') {
                        token.lexeme += (char) c;
                        state = 7;
                    } else if (c == '.' || c == ',' || c == ':' || c == ';' ||
                            c == '?' || c == '+' || c == '-' || c == '*' ||
                            c == '(' || c == ')' || c == '{' || c == '}' ||
                            c == '[' || c == ']') {
                        token.lexeme += (char) c;
                        state = 14;
                    } else if (c == '_' || Character.isLetter(c)) {
                        token.lexeme += (char) c;
                        state = 8;
                    } else if (Character.isDigit(c)) {
                        token.lexeme += (char) c;
                        state = 9;
                    } else if (c == '\'') {
                        state = 11;
                    } else if (c == '"') {
                        state = 13;
                    } else if (c == -1) {
                        token.type = Token.Type.END_OF_FILE;
                        state = 15;
                    } else {
                        token.lexeme += (char) c;
                        token.type = Token.Type.INVALID_TOKEN;
                        state = 15;
                    }

                    break;
                case 2:
                    if (c == '*') {
                        state = 3;
                    } else {
                        ungetc(c);
                        token.lexeme = "/";
                        state = 14;
                    }
                    break;
                case 3:
                    if (c == '*') {
                        state = 4;
                    } else if (c == -1) {
                        token.type = Token.Type.UNEXPECTED_EOF;
                        state = 15;
                    }
                    break;
                case 4:
                    if (c == '/') {
                        state = 1;
                    } else if (c == '*') {
                        state = 4;
                    } else if (c == -1) {
                        token.type = Token.Type.UNEXPECTED_EOF;
                        state = 15;
                    } else {
                        state = 3;
                    }
                    break;
                case 5:
                    if (c == '=') {
                        token.lexeme += (char) c;
                        state = 14;
                    } else {
                        ungetc(c);
                        state = 14;
                    }

                    break;
                case 6:
                    if (c == '&') {
                        token.lexeme += (char) c;
                        state = 14;
                    } else {
                        ungetc(c);
                        state = 15;
                        token.type = Token.Type.INVALID_TOKEN;
                    }

                    break;
                case 7:
                    if (c == '|') {
                        token.lexeme += (char) c;
                        state = 14;
                    } else {
                        ungetc(c);
                        state = 15;
                        token.type = Token.Type.INVALID_TOKEN;
                    }
                    break;
                case 8:
                    if (c == '_' || Character.isLetter(c) || Character.isDigit(c)) {
                        token.lexeme += (char) c;
                        state = 8;
                    } else {
                        ungetc(c);
                        state = 14;
                    }

                    break;
                case 9:
                    if (Character.isDigit(c)) {
                        token.lexeme += (char) c;
                        state = 9;
                    } else if (c == '.') {
                        token.lexeme += (char) c;
                        state = 10;
                    } else {
                        ungetc(c);
                        token.type = Token.Type.INTEGER_LITERAL;
                        token.literal = new Value(IntType.instance(), toInt(token.lexeme));
                        state = 15;
                    }

                    break;
                case 10:
                    if (Character.isDigit(c)) {
                        token.lexeme += (char) c;
                        state = 10;
                    } else {
                        ungetc(c);
                        token.type = Token.Type.FLOAT_LITERAL;
                        token.literal = new Value(FloatType.instance(), toFloat(token.lexeme));
                        state = 15;
                    }
                    break;
                case 11:
                    if (c != '\'') {
                        if (c == -1) {
                            token.type = Token.Type.UNEXPECTED_EOF;
                            state = 15;
                        } else {
                            if (c == '\n')
                                line++;

                            token.lexeme += (char) c;
                            state = 12;
                        }
                    } else {
                        token.lexeme += (char) c;
                        token.type = Token.Type.INVALID_TOKEN;
                        state = 15;
                    }

                    break;
                case 12:
                    if (c == -1) {
                        token.type = Token.Type.UNEXPECTED_EOF;
                        state = 15;
                    } else if (c == '\'') {
                        token.type = Token.Type.CHAR_LITERAL;
                        token.literal = new Value(CharType.instance(), token.lexeme.charAt(0));
                        state = 15;
                    } else {
                        token.type = Token.Type.INVALID_TOKEN;
                        state = 15;
                    }

                    break;
                case 13: // L ?
                    if (c == -1) {
                        token.type = Token.Type.UNEXPECTED_EOF;
                        state = 15;
                    } else if (c != '"') {
                        token.lexeme += (char) c;
                        state = 13;
                    } else if (c == '"') {
                        token.type = Token.Type.STRING_LITERAL;
                        token.literal = new Value(StringType.instance(), token.lexeme);
                        state = 15;
                    } else {
                        token.type = Token.Type.INVALID_TOKEN;
                        state = 15;
                    }
                    break;
                default:
                    throw new InternalException("Unreachable");
            }
        }

        if (state == 14)
            token.type = keywords.containsKey(token.lexeme) ? keywords.get(token.lexeme) : Token.Type.NAME;

        token.line = this.line;

        return token;
    }

    private int getc() {
        try {
            return input.read();
        } catch (Exception e) {
            throw new InternalException("Unable to read file");
        }
    }

    private void ungetc(int c) {
        if (c != -1) {
            try {
                input.unread(c);
            } catch (Exception e) {
                throw new InternalException("Unable to ungetc");
            }
        }
    }

    private int toInt(String lexeme) {
        try {
            return Integer.parseInt(lexeme);
        } catch (Exception e) {
            return 0;
        }
    }

    private float toFloat(String lexeme) {
        try {
            return Float.parseFloat(lexeme);
        } catch (Exception e) {
            return 0.0f;
        }
    }

}
