package interpreter.type.primitive;

import interpreter.type.Type;

public class IntType extends PrimitiveType {

    private static IntType type = new IntType();

    private IntType() {
        super(Type.Category.Int);
    }

    @Override
    public boolean match(Type type) {
        return type.equals(IntType.type);
    }

    @Override
    public String toString() {
        return "Int";
    }

    public static IntType instance() {
        return type;
    }

}
