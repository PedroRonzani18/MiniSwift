package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class DumpCommand extends Command {

    private Expr expr;

    public DumpCommand(int line, Expr expr) {
        super(line);
        this.expr = expr;
    }

    @Override
    public void execute() {
        Value value = expr.expr();
        System.out.println(value);
    }
    
}
