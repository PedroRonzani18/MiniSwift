package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class CastExpr extends Expr {

    public static enum Op {
        ToBool,
        ToInt,
        ToFloat,
        ToChar,
        ToString
    }

    private Op op;
    private Expr expr;

    public CastExpr(int line, Op op, Expr expr) {
        super(line);
        this.op = op;
        this.expr = expr;
    }

    @Override
    public Value expr() {
        Value exprValue = expr.expr();
        Value ret = null;

        switch (op) {
            case ToBool:
                ret = toBoolOp(exprValue);
                break;
            case ToInt:
                ret = toIntOp(exprValue);
                break;
            case ToFloat:
                ret = toFloatOp(exprValue);
                break;
            case ToChar:
                ret = toCharOp(exprValue);
                break;
            case ToString:
                ret = toStringOp(exprValue);
                break;

            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value toBoolOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case Int:

                int il = ((Integer) exprValue.data).intValue();
                return new Value(BoolType.instance(), !(il == 0));

            case Float:

                float fl = ((Float) exprValue.data).floatValue();
                return new Value(BoolType.instance(), !(fl == 0.0));

            case Char:

                char cl = ((Character) exprValue.data).charValue();
                return new Value(BoolType.instance(), !(cl == '0'));

            case Array:

                List<Object> all = ((ArrayList<Object>) exprValue.data);
                return new Value(BoolType.instance(), !(all.isEmpty()));

            case Dict:

                Map<Object, Object> hml = ((HashMap<Object, Object>) exprValue.data);
                return new Value(BoolType.instance(), !(hml.isEmpty()));

            default:
                return new Value(BoolType.instance(), true);
        }

    }

    private Value toIntOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case Int:

                int il = ((Integer) exprValue.data).intValue();
                return new Value(IntType.instance(), il);

            case Float:

                float fl = ((Float) exprValue.data).floatValue();
                return new Value(IntType.instance(), (int) fl);

            case Char:

                char cl = ((Character) exprValue.data).charValue();
                return new Value(IntType.instance(), (int) cl);

            default:
                return new Value(IntType.instance(), 0);
        }
    }

    private Value toFloatOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case Int:

                int il = ((Integer) exprValue.data).intValue();
                return new Value(FloatType.instance(), (float) il);

            case Float:

                float fl = ((Float) exprValue.data).floatValue();
                return new Value(FloatType.instance(), fl);

            case Char:

                char cl = ((Character) exprValue.data).charValue();
                return new Value(FloatType.instance(), (float) cl);

            default:
                return new Value(FloatType.instance(), 0.0);
        }

    }

    private Value toCharOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case Int:

                int il = ((Integer) exprValue.data).intValue();
                return new Value(CharType.instance(), (char) il);

            case Char:

                char cl = ((Character) exprValue.data).charValue();
                return new Value(CharType.instance(), cl);

            default:
                return new Value(CharType.instance(), '\0');
        }
    }

    private Value toStringOp(Value exprValue) {

        return new Value(StringType.instance(), exprValue.data.toString());
    }

}
