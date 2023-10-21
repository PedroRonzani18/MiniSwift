package interpreter.command;

import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.type.primitive.BoolType;
import interpreter.value.Value;

public class WhileCommand extends Command {

    private Expr expr;
    private Command cmds;

    public WhileCommand(int line, Expr expr, Command cmds) {
        super(line);
        this.expr = expr;
        this.cmds = cmds;
    }

    @Override
    public void execute() {
        do {
            Value value = expr.expr();
            BoolType boolType = BoolType.instance();
            if (boolType.match(value.type)) {
                boolean b = (Boolean) value.data;
                if (!b)
                    break;
                
                cmds.execute();
            } else {
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, value.type.toString());
            }
        } while (true);
    }

}
