package syntatic;

import static error.LanguageException.Error.InvalidLexeme;
import static error.LanguageException.Error.UnexpectedEOF;
import static error.LanguageException.Error.UnexpectedLexeme;

import error.LanguageException;
import interpreter.Environment;
import interpreter.Interpreter;
import interpreter.command.Command;
import lexical.LexicalAnalysis;
import lexical.Token;

public class SyntaticAnalysis {

    private LexicalAnalysis lex;
    private Token current;
    private Token previous;
    private Environment environment;

    public SyntaticAnalysis(LexicalAnalysis lex) {
        this.lex = lex;
        this.current = lex.nextToken();
        this.previous = null;
        this.environment = Interpreter.globals;
    }

    public Command process() {
        procCode();
        eat(Token.Type.END_OF_FILE);
        return null;
    }

    private void advance() {
        System.out.println("Found " + current);
        previous = current;
        current = lex.nextToken();
    }

    private void eat(Token.Type type) {
        if (type == current.type) {
            advance();
        } else {
            System.out.println("Expected (..., " + type + ", ..., ...), found " + current);
            reportError();
        }
    }

    private boolean check(Token.Type ...types) {
        for (Token.Type type : types) {
            if (current.type == type)
                return true;
        }

        return false;
    }

    private boolean match(Token.Type ...types) {
        if (check(types)) {
            advance();
            return true;
        } else {
            return false;
        }
    }

    private void reportError() {
        int line = current.line;
        switch (current.type) {
            case INVALID_TOKEN:
                throw LanguageException.instance(line, InvalidLexeme, current.lexeme);
            case UNEXPECTED_EOF:
            case END_OF_FILE:
                throw LanguageException.instance(line, UnexpectedEOF);
            default:
                throw LanguageException.instance(line, UnexpectedLexeme, current.lexeme);
        }
    }

    // <code> ::= { <cmd> }
    private void procCode() {
        while (check(Token.Type.OPEN_CUR,
                Token.Type.VAR, Token.Type.LET,
                Token.Type.PRINT, Token.Type.PRINTLN,
                Token.Type.DUMP, Token.Type.IF,
                Token.Type.WHILE, Token.Type.FOR,
                Token.Type.NOT, Token.Type.SUB,
                Token.Type.OPEN_PAR, Token.Type.FALSE,
                Token.Type.TRUE, Token.Type.INTEGER_LITERAL,
                Token.Type.FLOAT_LITERAL, Token.Type.CHAR_LITERAL,
                Token.Type.STRING_LITERAL, Token.Type.READ,
                Token.Type.RANDOM, Token.Type.TO_BOOL,
                Token.Type.TO_INT, Token.Type.TO_FLOAT,
                Token.Type.TO_CHAR, Token.Type.TO_STRING,
                Token.Type.ARRAY, Token.Type.DICT, Token.Type.NAME)) {
            procCmd();
        }
    }

    // <cmd> ::= <block> | <decl> | <print> | <dump> | <if> | <while> | <for> | <assign>
    private void procCmd() {
        if (check(Token.Type.OPEN_CUR)) {
            procBlock();
        } else if (check(Token.Type.VAR, Token.Type.LET)) {
            procDecl();
        } else if (check(Token.Type.PRINT, Token.Type.PRINTLN)) {
            procPrint();
        } else if (check(Token.Type.DUMP)) {
            procDump();
        } else if (check(Token.Type.IF)) {
            procIf();
        } else if (check(Token.Type.WHILE)) {
            procWhile();
        } else if (check(Token.Type.FOR)) {
            procFor();
        } else if (check(Token.Type.NOT, Token.Type.SUB,
                Token.Type.OPEN_PAR, Token.Type.FALSE,
                Token.Type.TRUE, Token.Type.INTEGER_LITERAL,
                Token.Type.FLOAT_LITERAL, Token.Type.CHAR_LITERAL,
                Token.Type.STRING_LITERAL, Token.Type.READ,
                Token.Type.RANDOM, Token.Type.TO_BOOL,
                Token.Type.TO_INT, Token.Type.TO_FLOAT,
                Token.Type.TO_CHAR, Token.Type.TO_STRING,
                Token.Type.ARRAY, Token.Type.DICT, Token.Type.NAME)) {
            procAssign();
        } else {
            reportError();
        }
    }

    // <block> ::= '{' <code> '}'
    private void procBlock() {
        eat(Token.Type.OPEN_CUR);
        procCode();
        eat(Token.Type.CLOSE_CUR);
    }

    // <decl> ::= <var> | <let>
    private void procDecl() {
        if (check(Token.Type.VAR)) {
            procVar();
        } else if (check(Token.Type.LET)) {
            procLet();
        } else {
            reportError();
        }
    }

    // <var> ::= var <name> ':' <type> [ '=' <expr> ] { ',' <name> ':' <type> [ '=' <expr> ] } [';']
    private void procVar() {
        eat(Token.Type.VAR);
        procName();
        eat(Token.Type.COLON);
        procType();

        if (match(Token.Type.ASSIGN)) {
            procExpr();
        }

        while (match(Token.Type.COMMA)) {
            procName();
            eat(Token.Type.COLON);
            procType();

            if (match(Token.Type.ASSIGN)) {
                procExpr();
            }
        }

        match(Token.Type.SEMICOLON);
    }

    // <let> ::= let <name> ':' <type> '=' <expr> { ',' <name> ':' <type> '=' <expr> } [';']
    private void procLet() {
    }

    // <print> ::= (print | println) '(' <expr> ')' [';']
    private void procPrint() {
        if (match(Token.Type.PRINT, Token.Type.PRINTLN)) {
            // Do nothing.
        } else {
            reportError();
        }

        eat(Token.Type.OPEN_PAR);
        procExpr();
        eat(Token.Type.CLOSE_PAR);

        match(Token.Type.SEMICOLON);
    }

    // <dump> ::= dump '(' <expr> ')' [';']
    private void procDump() {
        eat(Token.Type.DUMP);
        eat(Token.Type.OPEN_PAR);
        procExpr();
        eat(Token.Type.CLOSE_PAR);
        match(Token.Type.SEMICOLON);
    }

    // <if> ::= if <expr> <cmd> [ else <cmd> ]
    private void procIf() {
        eat(Token.Type.IF);
        procExpr();
        procCmd();
        if (match(Token.Type.ELSE)) {
            procCmd();
        }
    }

    // <while> ::= while <expr> <cmd>
    private void procWhile() {
        eat(Token.Type.WHILE);
        procExpr();
        procCmd();
    }

    // <for> ::= for ( <name> | ( var | let ) <name> ':' <type> ) in <expr> <cmd>
    private void procFor() {
    }

    // <assign> ::= [ <expr> '=' ] <expr> [ ';' ]
    private void procAssign() {
        procExpr();
        if (match(Token.Type.ASSIGN)) {
            procExpr();
        }

        match(Token.Type.SEMICOLON);
    }

    // <type> ::= <primitive> | <composed>
    private void procType() {
        if (check(Token.Type.BOOL, Token.Type.INT, Token.Type.FLOAT,
                Token.Type.CHAR, Token.Type.STRING)) {
            procPrimitive();
        } else if (check(Token.Type.ARRAY, Token.Type.DICT)) {
            procComposed();
        } else {
            reportError();
        }
    }

    // <primitive> ::= Bool | Int | Float | Char | String
    private void procPrimitive() {
        if (match(Token.Type.BOOL, Token.Type.INT,
                Token.Type.FLOAT, Token.Type.CHAR, Token.Type.STRING)) {
            // Do nothing.
        }
    }

    // <composed> ::= <arraytype> | <dicttype>
    private void procComposed() {
        if (check(Token.Type.ARRAY)) {
            procArrayType();
        } else if (check(Token.Type.DICT)) {
            procDictType();
        } else {
            reportError();
        }
    }

    // <arraytype> ::= Array '<' <type> '>'
    private void procArrayType() {
        eat(Token.Type.ARRAY);
        eat(Token.Type.LOWER_THAN);
        procType();
        eat(Token.Type.GREATER_THAN);
    }

    // <dicttype> ::= Dict '<' <type> ',' <type> '>'
    private void procDictType() {
        eat(Token.Type.DICT);
        eat(Token.Type.LOWER_THAN);
        procType();
        eat(Token.Type.COMMA);
        procType();
        eat(Token.Type.GREATER_THAN);
    }

    // <expr> ::= <cond> [ '?' <expr> ':' <expr> ]
    private void procExpr() {
        procCond();
        if (match(Token.Type.TERNARY)) {
            procExpr();
            eat(Token.Type.COLON);
            procExpr();
        }
    }

    // <cond> ::= <rel> { ( '&&' | '||' ) <rel> }
    private void procCond() {
        procRel();
        while (match(Token.Type.AND, Token.Type.OR)) {
            procRel();
        }
    }

    // <rel> ::= <arith> [ ( '<' | '>' | '<=' | '>=' | '==' | '!=' ) <arith> ]
    private void procRel() {
        procArith();

        // TODO: complete me!
    }

    // <arith> ::= <term> { ( '+' | '-' ) <term> }
    private void procArith() {
        procTerm();
        while (match(Token.Type.ADD, Token.Type.SUB)) {
            procTerm();
        }
    }

    // <term> ::= <prefix> { ( '*' | '/' ) <prefix> }
    private void procTerm() {
        procPrefix();

        // TODO: complete me!
    }

    // <prefix> ::= [ '!' | '-' ] <factor>
    private void procPrefix() {
        if (match(Token.Type.NOT, Token.Type.SUB)) {
            // Do nothing.
        }

        procFactor();
    }

    // <factor> ::= ( '(' <expr> ')' | <rvalue> ) <function>
    private void procFactor() {
        if (match(Token.Type.OPEN_PAR)) {
            procExpr();
            eat(Token.Type.CLOSE_PAR);
        } else {
            procRValue();
        }

        procFunction();
    }

    // <rvalue> ::= <const> | <action> | <cast> | <array> | <dict> | <lvalue>
    private void procRValue() {
        if (check(Token.Type.FALSE, Token.Type.TRUE,
                Token.Type.INTEGER_LITERAL, Token.Type.FLOAT_LITERAL,
                Token.Type.CHAR_LITERAL, Token.Type.STRING_LITERAL)) {
            procConst();
        } else if (check(Token.Type.READ, Token.Type.RANDOM)) {
            procAction();
        } else if (check(Token.Type.TO_BOOL, Token.Type.TO_INT,
                Token.Type.TO_FLOAT, Token.Type.TO_CHAR, Token.Type.TO_STRING)) {
            procCast();
        } else if (check(Token.Type.ARRAY)) {
            procArray();
        } else if (check(Token.Type.DICT)) {
            procDict();
        } else if (check(Token.Type.NAME)) {
            procLValue();
        } else {
            reportError();
        }
    }

    // <const> ::= <bool> | <int> | <float> | <char> | <string>
    private void procConst() {
        if (check(Token.Type.FALSE, Token.Type.TRUE)) {
            procBool();
        } else if (check(Token.Type.INTEGER_LITERAL)) {
            procInt();
        } else if (check(Token.Type.FLOAT_LITERAL)) {
            procFloat();
        } else if (check(Token.Type.CHAR_LITERAL)) {
            procChar();
        } else if (check(Token.Type.STRING_LITERAL)) {
            procString();
        } else {
            reportError();
        }
    }

    // <bool> ::= false | true
    private void procBool() {
        if (match(Token.Type.FALSE, Token.Type.TRUE)) {
            // Do nothing.
        } else {
            reportError();
        }
    }

    // <action> ::= ( read  | random ) '(' ')'
    private void procAction() {
        if (match(Token.Type.READ, Token.Type.RANDOM)) {
            // Do nothing.
        } else {
            reportError();
        }

        eat(Token.Type.OPEN_PAR);
        eat(Token.Type.CLOSE_PAR);
    }

    // <cast> ::= ( toBool | toInt | toFloat | toChar | toString ) '(' <expr> ')'
    private void procCast() {
    }

    // <array> ::= <arraytype> '(' [ <expr> { ',' <expr> } ] ')'
    private void procArray() {
        procArrayType();
        eat(Token.Type.OPEN_PAR);
        if (!check(Token.Type.CLOSE_PAR)) {
            procExpr();
            while (match(Token.Type.COMMA)) {
                procExpr();
            }
        }
        eat(Token.Type.CLOSE_PAR);
    }

    // <dict> ::= <dictype> '(' [ <expr> ':' <expr> { ',' <expr> ':' <expr> } ] ')'
    private void procDict() {
    }

    // <lvalue> ::= <name> { '[' <expr> ']' }
    private void procLValue() {
        procName();
        while (match(Token.Type.OPEN_BRA)) {
            procExpr();
            eat(Token.Type.CLOSE_BRA);
        }
    }

    // <function> ::= { '.' ( <fnoargs> | <fonearg> ) }
    private void procFunction() {
    }

    // <fnoargs> ::= ( count | empty | keys | values ) '(' ')'
    private void procFNoArgs() {
    }

    // <fonearg> ::= ( append | contains ) '(' <expr> ')'
    private void procFOneArg() {
    }

    private void procName() {
        eat(Token.Type.NAME);
    }

    private void procInt() {
        eat(Token.Type.INTEGER_LITERAL);
    }

    private void procFloat() {
        eat(Token.Type.FLOAT_LITERAL);
    }

    private void procChar() {
        eat(Token.Type.CHAR_LITERAL);
    }

    private void procString() {
        eat(Token.Type.STRING_LITERAL);
    }

}
