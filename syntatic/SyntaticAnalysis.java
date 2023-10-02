package syntatic;

import static error.LanguageException.Error.InvalidLexeme;
import static error.LanguageException.Error.UnexpectedEOF;
import static error.LanguageException.Error.UnexpectedLexeme;

import java.util.ArrayList;
import java.util.List;

import error.LanguageException;
import interpreter.Environment;
import interpreter.Interpreter;
import interpreter.command.BlocksCommand;
import interpreter.command.Command;
import interpreter.command.DumpCommand;
import interpreter.command.PrintCommand;
import interpreter.expr.ConstExpr;
import interpreter.expr.Expr;
import interpreter.expr.UnaryExpr;
import interpreter.type.primitive.BoolType;
import interpreter.value.Value;
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
        Command cmd = procCode();
        eat(Token.Type.END_OF_FILE);
        return cmd;
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
    private BlocksCommand procCode() {
        int line = current.line;
        List<Command> cmds = new ArrayList<Command>();

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
            Command cmd = procCmd();
            cmds.add(cmd);
        }

        BlocksCommand bcmd = new BlocksCommand(line, cmds);
        return bcmd;
    }

    // <cmd> ::= <block> | <decl> | <print> | <dump> | <if> | <while> | <for> | <assign>
    private Command procCmd() {
        Command cmd = null;
        if (check(Token.Type.OPEN_CUR)) {
            procBlock();
        } else if (check(Token.Type.VAR, Token.Type.LET)) {
            procDecl();
        } else if (check(Token.Type.PRINT, Token.Type.PRINTLN)) {
            cmd = procPrint();
        } else if (check(Token.Type.DUMP)) {
            cmd = procDump();
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

        return cmd;
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
        eat(Token.Type.LET);
        procName();
        eat(Token.Type.COLON);
        procType();
        eat(Token.Type.ASSIGN);
        procExpr();

        while(match(Token.Type.COMMA)) {
            procName();
            eat(Token.Type.COLON);
            procType();
            eat(Token.Type.ASSIGN);
            procExpr();
        }

        match(Token.Type.SEMICOLON);
    }

    // <print> ::= (print | println) '(' <expr> ')' [';']
    private PrintCommand procPrint() {
        boolean newline = false;
        if (match(Token.Type.PRINT, Token.Type.PRINTLN)) {
            newline = (previous.type == Token.Type.PRINTLN);
        } else {
            reportError();
        }
        int line = previous.line;

        eat(Token.Type.OPEN_PAR);
        Expr expr = procExpr();
        eat(Token.Type.CLOSE_PAR);

        match(Token.Type.SEMICOLON);

        PrintCommand pcmd = new PrintCommand(line, expr, newline);
        return pcmd;
    }

    // <dump> ::= dump '(' <expr> ')' [';']
    private DumpCommand procDump() {
        eat(Token.Type.DUMP);
        int line = previous.line;
        eat(Token.Type.OPEN_PAR);
        Expr expr = procExpr();
        eat(Token.Type.CLOSE_PAR);
        match(Token.Type.SEMICOLON);

        DumpCommand dcmd = new DumpCommand(line, expr);
        return dcmd;
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
        eat(Token.Type.FOR);
        if(check(Token.Type.NAME)){
            procName();
        } else if(match(Token.Type.VAR, Token.Type.LET)){
            procName();
            eat(Token.Type.COLON);
            procType();
        } else {
            reportError();
        }

        eat(Token.Type.IN);
        procExpr();
        procCmd();
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
    private Expr procExpr() {
        Expr expr = procCond();

        if (match(Token.Type.TERNARY)) {
            procExpr();
            eat(Token.Type.COLON);
            procExpr();
        }

        return expr;
    }

    // <cond> ::= <rel> { ( '&&' | '||' ) <rel> }
    private Expr procCond() {
        Expr expr = procRel();
        while (match(Token.Type.AND, Token.Type.OR)) {
            procRel();
        }

        return expr;
    }

    // <rel> ::= <arith> [ ( '<' | '>' | '<=' | '>=' | '==' | '!=' ) <arith> ]
    private Expr procRel() {
        Expr expr = procArith();

        if(match(Token.Type.LOWER_THAN, Token.Type.GREATER_THAN, Token.Type.LOWER_EQUAL, Token.Type.GREATER_EQUAL, Token.Type.EQUALS, Token.Type.NOT_EQUALS)){
            procArith();
        }

        return expr;
    }

    // <arith> ::= <term> { ( '+' | '-' ) <term> }
    private Expr procArith() {
        Expr expr = procTerm();
        while (match(Token.Type.ADD, Token.Type.SUB)) {
            procTerm();
        }

        return expr;
    }

    // <term> ::= <prefix> { ( '*' | '/' ) <prefix> }
    private Expr procTerm() {
        Expr expr = procPrefix();

        while(match(Token.Type.MUL, Token.Type.DIV)){
            procPrefix();
        }

        return expr;
    }

    // <prefix> ::= [ '!' | '-' ] <factor>
    private Expr procPrefix() {
        UnaryExpr.Op op = null;
        int line = -1;
        if (match(Token.Type.NOT, Token.Type.SUB)) {
            switch (previous.type) {
                case NOT:
                    op = UnaryExpr.Op.Not;
                    break;
                case SUB:
                    op = UnaryExpr.Op.Neg;
                    break;
                default:
                    reportError();
            }

            line = previous.line;
        }

        Expr expr = procFactor();

        if (op != null)
            expr = new UnaryExpr(line, expr, op);

        return expr;
    }

    // <factor> ::= ( '(' <expr> ')' | <rvalue> ) <function>
    private Expr procFactor() {
        Expr expr = null;
        if (match(Token.Type.OPEN_PAR)) {
            procExpr();
            eat(Token.Type.CLOSE_PAR);
        } else {
            expr = procRValue();
        }

        procFunction();

        return expr;
    }

    // <rvalue> ::= <const> | <action> | <cast> | <array> | <dict> | <lvalue>
    private Expr procRValue() {
        Expr expr = null;
        if (check(Token.Type.FALSE, Token.Type.TRUE,
                Token.Type.INTEGER_LITERAL, Token.Type.FLOAT_LITERAL,
                Token.Type.CHAR_LITERAL, Token.Type.STRING_LITERAL)) {
            expr = procConst();
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

        return expr;
    }

    // <const> ::= <bool> | <int> | <float> | <char> | <string>
    private ConstExpr procConst() {
        Value value = null;
        if (check(Token.Type.FALSE, Token.Type.TRUE)) {
            value = procBool();
        } else if (check(Token.Type.INTEGER_LITERAL)) {
            value = procInt();
        } else if (check(Token.Type.FLOAT_LITERAL)) {
            value = procFloat();
        } else if (check(Token.Type.CHAR_LITERAL)) {
            value = procChar();
        } else if (check(Token.Type.STRING_LITERAL)) {
            value = procString();
        } else {
            reportError();
        }

        ConstExpr cexpr = new ConstExpr(previous.line, value);
        return cexpr;
    }

    // <bool> ::= false | true
    private Value procBool() {
        Value value = null;
        if (match(Token.Type.FALSE, Token.Type.TRUE)) {
            switch (previous.type) {
                case FALSE:
                    value = new Value(BoolType.instance(), false);
                    break;
                case TRUE:
                    value = new Value(BoolType.instance(), true);
                    break;
                default:
                    reportError();
            }
        } else {
            reportError();
        }

        return value;
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
        if(match(Token.Type.TO_BOOL, Token.Type.TO_INT, Token.Type.TO_FLOAT, Token.Type.TO_CHAR, Token.Type.TO_STRING)){
            // Do nothing.
        } else {
            reportError();
        }
        eat(Token.Type.OPEN_PAR);
        procExpr();
        eat(Token.Type.CLOSE_PAR);
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
        procDictType();
        eat(Token.Type.OPEN_PAR);
        if(!check(Token.Type.CLOSE_PAR)){
            procExpr();
            eat(Token.Type.COMMA);
            procExpr();
            
            while(match(Token.Type.COMMA)){
                procExpr();
                eat(Token.Type.COMMA);
                procExpr();
            }
        }
        eat(Token.Type.CLOSE_PAR);
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
        while(match(Token.Type.DOT)) {
            if(check(Token.Type.COUNT, Token.Type.EMPTY, Token.Type.KEYS, Token.Type.VALUES)) {
                procFNoArgs();
            } else if (check(Token.Type.APPEND, Token.Type.CONTAINS)) {
                procFOneArg();
            } else {
                reportError();
            }
        }
    }

    // <fnoargs> ::= ( count | empty | keys | values ) '(' ')'
    private void procFNoArgs() {
        if(match(Token.Type.COUNT, Token.Type.EMPTY, Token.Type.KEYS, Token.Type.VALUES)){
            // Do nothing.
        } else {
            reportError();
        }
        
        eat(Token.Type.OPEN_PAR);
        eat(Token.Type.CLOSE_PAR);
    }

    // <fonearg> ::= ( append | contains ) '(' <expr> ')'
    private void procFOneArg() {
        if(match(Token.Type.APPEND, Token.Type.CONTAINS)){
            // Do nothing.
        } else {
            reportError();
        }

        eat(Token.Type.OPEN_PAR);
        procExpr();
        eat(Token.Type.CLOSE_PAR);
    }

    private void procName() {
        eat(Token.Type.NAME);
    }

    private Value procInt() {
        Value v = current.literal;
        eat(Token.Type.INTEGER_LITERAL);
        return v;
    }

    private Value procFloat() {
        Value v = current.literal;
        eat(Token.Type.FLOAT_LITERAL);
        return v;
    }

    private Value procChar() {
        Value v = current.literal;
        eat(Token.Type.CHAR_LITERAL);
        return v;
    }

    private Value procString() {
        Value v = current.literal;
        eat(Token.Type.STRING_LITERAL);
        return v;
    }

}
