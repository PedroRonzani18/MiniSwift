package interpreter.type.primitive;

import interpreter.type.Type;

public class BoolType extends PrimitiveType {

    private static BoolType type = new BoolType();

    private BoolType() {
        super(Type.Category.Bool);
    }

    @Override
    public boolean match(Type type) {
        return type.equals(BoolType.type);
    }

    @Override
    public String toString() {
        return "Bool";
    }

    public static BoolType instance() {
        return type;
    }

}
