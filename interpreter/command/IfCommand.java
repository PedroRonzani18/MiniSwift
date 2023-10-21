package interpreter.command;

import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.type.primitive.BoolType;
import interpreter.value.Value;

public class IfCommand extends Command {
    private Expr expr;
    private Command thenCmds;
    private Command elseCmds;

    public IfCommand(int line, Expr expr, Command thenCmds) {
        super(line);
        this.expr = expr;
        this.thenCmds = thenCmds;
        this.elseCmds = null;
    }

    public IfCommand(int line, Expr expr, Command thenCmds, Command elseCmds) {
        super(line);
        this.expr = expr;
        this.thenCmds = thenCmds;
        this.elseCmds = elseCmds;
    }

    @Override
    public void execute() {

        Value value = expr.expr();
        BoolType boolType = BoolType.instance();

        if (!boolType.match(value.type))
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    value.type.toString());

        boolean b = (Boolean) value.data;

        if (b)
            thenCmds.execute();
        else if (elseCmds != null)
            elseCmds.execute();

    }
}
