package interpreter.expr;

import error.LanguageException;
import interpreter.value.Value;

public class ConditionalExpr extends Expr {

    private Expr cond;
    private Expr trueExpr;
    private Expr falseExpr;

    public ConditionalExpr(int line, Expr cond, Expr trueExpr, Expr falseExpr) {
        super(line);
        this.cond = cond;
        this.trueExpr = trueExpr;
        this.falseExpr = falseExpr;
    }

    public Value expr() {
        Value condValue = cond.expr();

        switch (condValue.type.getCategory()) {

            case Bool:
                return ((Boolean) condValue.data).booleanValue() ? trueExpr.expr() : falseExpr.expr();

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }

    }
}
