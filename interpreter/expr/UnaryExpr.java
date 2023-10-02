package interpreter.expr;

import error.LanguageException;
import interpreter.type.primitive.BoolType;
import interpreter.value.Value;

public class UnaryExpr extends Expr {

    public static enum Op {
        Not,
        Neg
    }

    private Expr expr;
    private Op op;
    
    public UnaryExpr(int line, Expr expr, Op op) {
        super(line);
        this.expr = expr;
        this.op = op;
    }

    @Override
    public Value expr() {
        Value value = expr.expr();
        Value ret = null;
        switch (op) {
            case Not:
                ret = notOp(value);
                break;
            case Neg:
                ret = negOp(value);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value notOp(Value value) {
        BoolType btype = BoolType.instance();
        if (btype.match(value.type)) {
            boolean b = ((Boolean) value.data).booleanValue();
            return new Value(btype, !b);
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, value.type.toString());
        }
    }

    private Value negOp(Value value) {
        switch (value.type.getCategory()) {
            case Int:
                int n = ((Integer) value.data).intValue();
                return new Value(value.type, -n);
            case Float:
                float f = ((Float) value.data).floatValue();
                return new Value(value.type, -f);
            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType, value.type.toString());
        }
    }

}
