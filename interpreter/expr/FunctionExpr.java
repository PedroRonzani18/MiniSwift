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

    public Expr getArg() {
        return this.arg;
    }

    public Op getOp() { 
        return this.op; 
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

                String sl = ((String) exprValue.data);
                return new Value(IntType.instance(), sl.length());

            case Array:

                ArrayList<Object> al = ((ArrayList<Object>) exprValue.data);
                return new Value(IntType.instance(), al.size());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }

    }

    public Value emptyOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
            case String:

                String sl = ((String) exprValue.data);
                return new Value(BoolType.instance(), sl.isEmpty());

            case Array:

                List<Object> ll = ((ArrayList<Object>) exprValue.data);
                return new Value(BoolType.instance(), ll.isEmpty());

            case Dict:

                Map<Object, Object> ml = ((HashMap<Object, Object>) exprValue.data);
                return new Value(BoolType.instance(), ml.isEmpty());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value keysOp(Value exprValue) {

        switch (exprValue.type.getCategory()) {
    
            case Dict:
            
                Map<Object, Object> ml = ((HashMap<Object, Object>) exprValue.data);

                List<Object> keysList = new ArrayList<Object>(ml.keySet());

                return new Value(ArrayType.instance(((DictType)exprValue.type).getKeyType()), keysList);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public Value valuesOp(Value exprValue) {
        switch (exprValue.type.getCategory()) {

            case Dict:

                Map<Object, Object> ml = ((HashMap<Object, Object>) exprValue.data);

                List<Object> valuesList = new ArrayList<>(ml.values());

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

                List<Object> ll = ((ArrayList<Object>) exprValue.data);

                ll.add(argValue.data);

                return new Value(exprValue.type, ll);

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

                List<Object> ll = ((ArrayList<Object>) exprValue.data);

                return new Value(BoolType.instance(), ll.contains(argValue.data));

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }

    }
}
