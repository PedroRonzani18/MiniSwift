package interpreter.expr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.Type;
import interpreter.type.composed.ArrayType;
import interpreter.type.composed.DictType;
import interpreter.type.primitive.CharType;
import interpreter.type.primitive.IntType;
import interpreter.type.primitive.StringType;
import interpreter.value.Value;

public class AccessExpr extends SetExpr {
    private SetExpr base;
    private Expr index;

    public AccessExpr(int line, SetExpr base, Expr index) {
        super(line);
        this.base = base;
        this.index = index;
    }

    public Value expr() {

        Value baseValue = base.expr();
        Value indexValue = index.expr();

        switch (baseValue.type.getCategory()) {

            case String:

                if (!(indexValue.type.match(IntType.instance())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                String str = (String) baseValue.data;

                int sindex = (int) indexValue.data;

                if (sindex < 0 || str.length() <= sindex)
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                return new Value(CharType.instance(), str.charAt((int) indexValue.data));

            case Array:

                if (!(indexValue.type.match(IntType.instance())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                List<Object> arr = ((ArrayList<Object>) baseValue.data);

                int aindex = (int) indexValue.data;

                if (aindex < 0 || arr.size() <= aindex)
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                return new Value(((ArrayType) baseValue.type).getInnerType(), arr.get((int) indexValue.data));

            case Dict:

                if (!(indexValue.type.match(((DictType) baseValue.type).getKeyType())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                Map<Object, Object> dict = ((HashMap<Object, Object>) baseValue.data);

                Object key = indexValue.data;

                if (!dict.containsKey(key))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                return new Value(((DictType) baseValue.type).getValueType(), dict.get(indexValue.data));

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }

    public void setValue(Value value) {
        Value baseValue = base.expr();
        Value indexValue = index.expr();
        int index;

        switch (baseValue.type.getCategory()) {

            case String:

                if (!(indexValue.type.match(IntType.instance())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                if (!(value.type.match(CharType.instance())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            value.type.toString());

                String str = (String) baseValue.data;

                index = (int) indexValue.data;

                if (index < 0 || str.length() <= index)
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                char[] charArray = str.toCharArray();
                charArray[index] = (Character) value.data;
                str = new String(charArray);

                base.setValue(new Value(StringType.instance(), str));

                break;

            case Array:

                if (!(indexValue.type.match(IntType.instance())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                Type innerType = ((ArrayType) baseValue.type).getInnerType();

                if (!(value.type.match(innerType)))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                List<Object> arr = ((ArrayList<Object>) baseValue.data);

                index = (int) indexValue.data;

                if (index < 0 || arr.size() <= index)
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                arr.set(index, value.data);

                break;

            case Dict:

                if (!(indexValue.type.match(((DictType) baseValue.type).getKeyType())))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            indexValue.type.toString());

                Map<Object, Object> mp = ((HashMap<Object, Object>) baseValue.data);

                Type innerValueType = ((DictType) baseValue.type).getValueType();

                if (!(value.type.match(innerValueType)))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

                Object key = indexValue.data;

                mp.put(key, value.data);

                break;

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

        }
    }
}
