package interpreter;

import static error.LanguageException.Error.AlreadyDeclaredVariable;
import static error.LanguageException.Error.UndeclaredVariable;

import java.util.HashMap;
import java.util.Map;

import error.LanguageException;
import interpreter.expr.Variable;
import interpreter.type.Type;
import lexical.Token;

public class Environment {

    private final Environment enclosing;
    private final Map<String, Variable> memory = new HashMap<>();

    public Environment() {
        this(null);
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }

    public Variable declare(Token name, Type type, boolean constant) {
        if (memory.containsKey(name.lexeme))
            throw LanguageException.instance(name.line, AlreadyDeclaredVariable, name.lexeme);

        Variable var = new Variable(name, type, constant);
        memory.put(name.lexeme, var);

        return var;
    }

    public Variable get(Token name) {
        if (memory.containsKey(name.lexeme))
            return memory.get(name.lexeme);

        if (enclosing != null)
            return enclosing.get(name);

        throw LanguageException.instance(name.line, UndeclaredVariable, name.lexeme);
    }

}
