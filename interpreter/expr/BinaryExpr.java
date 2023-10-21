package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

    public static enum BinaryOp {
        AndOp,
        OrOp,
        EqualOp,
        NotEqualOp,
        LowerThanOp,
        LowerEqualOp,
        GreaterThanOp,
        GreaterEqualOp,
        AddOp,
        SubOp,
        MulOp,
        DivOp
    }

    private Expr left;
    private Expr right;
    private BinaryOp op;

    public BinaryExpr(int line, Expr left, BinaryOp op, Expr right) {

        super(line);
        this.left = left;
        this.right = right;
        this.op = op;
    }

    public Value expr() {

        Value leftValue = left.expr();
        Value rightValue = right.expr();
        Value ret = null;

        if (!leftValue.type.match(rightValue.type))
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    rightValue.type.toString());

        switch (op) {
            case AndOp:
                ret = andOp(leftValue, rightValue);
                break;
            case OrOp:
                ret = orOp(leftValue, rightValue);
                break;
            case EqualOp:
                ret = equalOp(leftValue, rightValue);
                break;
            case NotEqualOp:
                ret = notEqualOp(leftValue, rightValue);
                break;
            case LowerThanOp:
                ret = lowerThanOp(leftValue, rightValue);
                break;
            case LowerEqualOp:
                ret = lowerEqualOp(leftValue, rightValue);
                break;
            case GreaterThanOp:
                ret = greaterThanOp(leftValue, rightValue);
                break;
            case GreaterEqualOp:
                ret = greaterEqualOp(leftValue, rightValue);
                break;
            case AddOp:
                ret = addOp(leftValue, rightValue);
                break;
            case SubOp:
                ret = subOp(leftValue, rightValue);
                break;
            case MulOp:
                ret = mulOp(leftValue, rightValue);
                break;
            case DivOp:
                ret = divOp(leftValue, rightValue);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value andOp(Value leftValue, Value rightValue) {

        BoolType btype = BoolType.instance();
        if (btype.match(leftValue.type)) {
            boolean il = ((Boolean) leftValue.data).booleanValue();
            boolean ir = ((Boolean) rightValue.data).booleanValue();
            return new Value(btype, il && ir);
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    leftValue.type.toString());
        }
    }

    private Value orOp(Value leftValue, Value rightValue) {

        BoolType btype = BoolType.instance();
        if (btype.match(leftValue.type)) {
            boolean il = ((Boolean) leftValue.data).booleanValue();
            boolean ir = ((Boolean) rightValue.data).booleanValue();
            return new Value(btype, il || ir);
        } else {
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    leftValue.type.toString());
        }
    }

    private Value equalOp(Value leftValue, Value rightValue) {
        return new Value(BoolType.instance(), leftValue.data.equals(rightValue.data));
    }

    private Value notEqualOp(Value leftValue, Value rightValue) {
        return new Value(BoolType.instance(), !leftValue.data.equals(rightValue.data));
    }

    private Value lowerThanOp(Value leftValue, Value rightValue) {
        
        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il < ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl < fr);

            case Char:
                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), cl < cr);

            case String:
                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(CharType.instance(), sl.length() < sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        rightValue.type.toString());
        }
    }

    private Value lowerEqualOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il <= ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl <= fr);

            case Char:
                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), cl <= cr);

            case String:
                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(CharType.instance(), sl.length() <= sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        rightValue.type.toString());
        }
    }

    private Value greaterThanOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il > ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl > fr);

            case Char:
                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), cl > cr);

            case String:
                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(CharType.instance(), sl.length() > sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation,
                        rightValue.type.toString());
        }
    }

    private Value greaterEqualOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il >= ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl >= fr);

            case Char:
                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), cl >= cr);

            case String:
                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(CharType.instance(), sl.length() >= sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation,
                        rightValue.type.toString());
        }
    }

    private Value addOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il + ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl + fr);

            case Char:
                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), (char)(cl + cr));

            case String:
                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(CharType.instance(), sl + sr);

            case Array: 
                List<Object> al = ((ArrayList<Object>) leftValue.data);
                List<Object> ar = ((ArrayList<Object>) rightValue.data);

                List<Object> aResult = new ArrayList<>();
                aResult.addAll(al);
                aResult.addAll(ar);

                return new Value(leftValue.type, aResult);

            case Dict:
                Map<Object, Object> ml = ((Map<Object, Object>) leftValue.data);
                Map<Object, Object> mr = ((Map<Object, Object>) rightValue.data);

                Map<Object, Object> mResult = new HashMap<>();
                mResult.putAll(ml);
                mResult.putAll(mr);

                return new Value(leftValue.type, mResult);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation,
                        leftValue.type.toString());
        }

    }

    private Value subOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il - ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl - fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation,
                        leftValue.type.toString());
        }
    }

    private Value mulOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il * ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl * fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

    private Value divOp(Value leftValue, Value rightValue) {

        switch (leftValue.type.getCategory()) {
            case Int:
                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il / ir);

            case Float:
                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl / fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

}