package interpreter.expr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.Type.Category;
import interpreter.type.primitive.BoolType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.FloatType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class BinaryExpr extends Expr {

    public static enum Op {
        And,
        Or,
        Equal,
        NotEqual,
        LowerThan,
        LowerEqual,
        GreaterThan,
        GreaterEqual,
        Add,
        Sub,
        Mul,
        Div
    }

    private Expr left;
    private Op op;
    private Expr right;

    public BinaryExpr(int line, Expr left, Op op, Expr right) {
        super(line);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public Value expr() {

        Value leftValue = left.expr();
        Value rightValue = right.expr();
        Value ret = null;

        switch (op) {
            case And:
                ret = andOp(leftValue, rightValue);
                break;
            case Or:
                ret = orOp(leftValue, rightValue);
                break;
            case Equal:
                ret = equalOp(leftValue, rightValue);
                break;
            case NotEqual:
                ret = notEqualOp(leftValue, rightValue);
                break;
            case LowerThan:
                ret = lowerThanOp(leftValue, rightValue);
                break;
            case LowerEqual:
                ret = lowerEqualOp(leftValue, rightValue);
                break;
            case GreaterThan:
                ret = greaterThanOp(leftValue, rightValue);
                break;
            case GreaterEqual:
                ret = greaterEqualOp(leftValue, rightValue);
                break;
            case Add:
                ret = addOp(leftValue, rightValue);
                break;
            case Sub:
                ret = subOp(leftValue, rightValue);
                break;
            case Mul:
                ret = mulOp(leftValue, rightValue);
                break;
            case Div:
                ret = divOp(leftValue, rightValue);
                break;
            default:
                throw new InternalError("unreachable");
        }

        return ret;
    }

    private Value andOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Bool);

        switch (leftValue.type.getCategory()) {
            case Bool:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                boolean il = ((Boolean) leftValue.data).booleanValue();
                boolean ir = ((Boolean) rightValue.data).booleanValue();

                return new Value(BoolType.instance(), il && ir);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value orOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Bool);

        switch (leftValue.type.getCategory()) {
            case Bool:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                boolean il = ((Boolean) leftValue.data).booleanValue();
                boolean ir = ((Boolean) rightValue.data).booleanValue();

                return new Value(BoolType.instance(), il || ir);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value equalOp(Value leftValue, Value rightValue) {

        if (!leftValue.type.match(rightValue.type))
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    rightValue.type.toString());

        return new Value(BoolType.instance(), leftValue.data.equals(rightValue.data));
    }

    private Value notEqualOp(Value leftValue, Value rightValue) {

        if (!leftValue.type.match(rightValue.type))
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                    rightValue.type.toString());

        return new Value(BoolType.instance(), !leftValue.data.equals(rightValue.data));
    }

    private Value lowerThanOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float, Category.Char, Category.String);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(BoolType.instance(), il < ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(BoolType.instance(), fl < fr);

            case Char:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(BoolType.instance(), cl < cr);

            case String:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(BoolType.instance(), sl.length() < sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

    private Value lowerEqualOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float, Category.Char, Category.String);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(BoolType.instance(), il <= ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(BoolType.instance(), fl <= fr);

            case Char:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(BoolType.instance(), cl <= cr);

            case String:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(BoolType.instance(), sl.length() <= sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

    private Value greaterThanOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float, Category.Char, Category.String);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(BoolType.instance(), il > ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(BoolType.instance(), fl > fr);

            case Char:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(BoolType.instance(), cl > cr);

            case String:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(BoolType.instance(), sl.length() > sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value greaterEqualOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float, Category.Char, Category.String);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(BoolType.instance(), il >= ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(BoolType.instance(), fl >= fr);

            case Char:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(BoolType.instance(), cl >= cr);

            case String:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(BoolType.instance(), sl.length() >= sr.length());

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value addOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float, Category.Char, Category.String,
                Category.Array, Category.Dict);

        switch (leftValue.type.getCategory()) {

            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il + ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl + fr);

            case Char:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                char cl = ((Character) leftValue.data).charValue();
                char cr = ((Character) rightValue.data).charValue();

                return new Value(CharType.instance(), (char) (cl + cr));

            case String:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                String sl = ((String) leftValue.data);
                String sr = ((String) rightValue.data);

                return new Value(StringType.instance(), sl + sr);

            case Array:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                List<Object> al = ((ArrayList<Object>) leftValue.data);
                List<Object> ar = ((ArrayList<Object>) rightValue.data);

                List<Object> aResult = new ArrayList<>();
                aResult.addAll(al);
                aResult.addAll(ar);

                return new Value(leftValue.type, aResult);

            case Dict:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                Map<Object, Object> ml = ((Map<Object, Object>) leftValue.data);
                Map<Object, Object> mr = ((Map<Object, Object>) rightValue.data);

                Map<Object, Object> mResult = new HashMap<>();
                mResult.putAll(ml);
                mResult.putAll(mr);

                return new Value(leftValue.type, mResult);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value subOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il - ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl - fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    private Value mulOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il * ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl * fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

    private Value divOp(Value leftValue, Value rightValue) {

        List<Category> allowedCategories = Arrays.asList(Category.Int, Category.Float);

        switch (leftValue.type.getCategory()) {
            case Int:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                int il = ((Integer) leftValue.data).intValue();
                int ir = ((Integer) rightValue.data).intValue();

                return new Value(IntType.instance(), il / ir);

            case Float:

                if (!allowedCategories.contains(rightValue.type.getCategory()))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                if (!leftValue.type.match(rightValue.type))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            rightValue.type.toString());

                float fl = ((Float) leftValue.data).floatValue();
                float fr = ((Float) rightValue.data).floatValue();

                return new Value(FloatType.instance(), fl / fr);

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        leftValue.type.toString());
        }
    }

}