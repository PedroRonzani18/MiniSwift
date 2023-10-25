package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.IntType;
import interpreter.value.Value;

public class FunctionExpr extends Expr {

    public static enum Op {
        Count,
        Empty,
        Keys,
        Values,
        Append,
        Contains
    }

    private Op op;
    private Expr expr;
    private Expr arg;

    public FunctionExpr(int line, Op op, Expr expr, Expr arg) {
        super(line);
        this.op = op;
        this.expr = expr;
        this.arg = arg;
    }

    public FunctionExpr(int line, Op op, Expr expr) {
        super(line);
        this.op = op;
        this.expr = expr;
        this.arg = null;
    }

    @Override
    public Value expr() {

        Value exprValue = expr.expr();
        Value argValue = arg != null ? arg.expr() : null;
        Value ret = null;

        switch (op) {
            case Count:
                ret = countOp(exprValue);
                break;
            case Empty:
                ret = emptyOp(exprValue);
                break;
            case Keys:
                ret = keysOp(exprValue);
                break;
            case Values:
                ret = valuesOp(exprValue);
                break;
            case Append:
                ret = appendOp(exprValue, argValue);
                break;
            case Contains:
                ret = containsOp(exprValue, argValue);
                break;

            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    public Value countOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case String:

                String str = ((String) exprValue.data);
                return new Value(IntType.instance(), str.length());

            case Array:

                ArrayList<Object> arr = ((ArrayList<Object>) exprValue.data);
                return new Value(IntType.instance(), arr.size());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value emptyOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case String:

                String str = ((String) exprValue.data);
                return new Value(BoolType.instance(), str.isEmpty());

            case Array:

                List<Object> arr = ((ArrayList<Object>) exprValue.data);
                return new Value(BoolType.instance(), arr.isEmpty());

            case Dict:

                Map<Object, Object> mp = ((HashMap<Object, Object>) exprValue.data);
                return new Value(BoolType.instance(), mp.isEmpty());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value keysOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
    
            case Dict:
            
                Map<Object, Object> mp = ((HashMap<Object, Object>) exprValue.data);

                List<Object> keysList = new ArrayList<Object>(mp.keySet());

                return new Value(ArrayType.instance(((DictType)exprValue.type).getKeyType()), keysList);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value valuesOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {

            case Dict:

                Map<Object, Object> mp = ((HashMap<Object, Object>) exprValue.data);

                List<Object> valuesList = new ArrayList<>(mp.values());

                return new Value(ArrayType.instance(((DictType)exprValue.type).getValueType()), valuesList);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value appendOp(Value exprValue, Value argValue) {

        switch (exprValue.type.getCategory()) {

            case Array:

                if (!argValue.type.match(((ArrayType) exprValue.type).getInnerType()))
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        argValue.type.toString());

                List<Object> arr = ((ArrayList<Object>) exprValue.data);

                arr.add(argValue.data);

                return new Value(exprValue.type, arr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value containsOp(Value exprValue, Value argValue) {

        switch (exprValue.type.getCategory()) {

            case Array:

                if (!argValue.type.match(((ArrayType) exprValue.type).getInnerType()))
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        argValue.type.toString());

                List<Object> arr = ((ArrayList<Object>) exprValue.data);

                return new Value(BoolType.instance(), arr.contains(argValue.data));

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }
}
