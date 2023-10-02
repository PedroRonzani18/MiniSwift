package interpreter.command;

import interpreter.expr.Expr;
import interpreter.value.Value;

public class PrintCommand extends Command {

    private Expr expr;
    private boolean newline;

    public PrintCommand(int line, Expr expr, boolean newline) {
        super(line);
        this.expr = expr;
        this.newline = newline;
    }

    @Override
    public void execute() {
        Value value = expr.expr();
        System.out.print(value.data);
        if (newline)
            System.out.println();
    }
    
}
