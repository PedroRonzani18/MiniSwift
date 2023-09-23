package interpreter.type.primitive;

import interpreter.type.Type;
import interpreter.type.TypeException;

public abstract class PrimitiveType extends Type {

    protected PrimitiveType(Type.Category classification) {
        super(classification);
    }

    public static PrimitiveType instance(Type.Category classification) {
        switch (classification) {
            case Bool:
                return BoolType.instance();
            case Int:
                return IntType.instance();
            case Float:
                return FloatType.instance();
            case Char:
                return CharType.instance();
            case String:
                return StringType.instance();
            default:
                throw new TypeException();
        }
    }

}
