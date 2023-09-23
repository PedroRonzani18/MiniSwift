package interpreter.expr;

import static error.LanguageException.Error.ConstantAssignment;
import static error.LanguageException.Error.InvalidType;
import static error.LanguageException.Error.UnitializedVariable;

import error.LanguageException;
import interpreter.type.Type;
import interpreter.value.Value;
import lexical.Token;

public class Variable extends SetExpr {

    private String name;
    private Type type;
    private boolean constant;
    private Value value;

    public Variable(Token name, Type type, boolean constant) {
        super(name.line);

        this.name = name.lexeme;
        this.type = type;
        this.constant = constant;
        this.value = null;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return type;
    }

    public boolean isConstant() {
        return this.constant;
    }

    public void initialize(Value value) {
        this.write(value, true);
    }

    public Value expr() {
        if (this.value == null)
            throw LanguageException.instance(super.getLine(), UnitializedVariable, name);

        return this.value;
    }

    public void setValue(Value value) {
        this.write(value, false);
    }

    private void write(Value value, boolean initialize) {
        if (!initialize && this.isConstant())
            throw LanguageException.instance(super.getLine(), ConstantAssignment, name);

        if (!this.type.match(value.type))
            throw LanguageException.instance(super.getLine(), InvalidType, value.type.toString());

        this.value = value;
    }

}
