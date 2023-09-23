package interpreter.type.primitive;

import interpreter.type.Type;

public class FloatType extends PrimitiveType {

    private static FloatType type = new FloatType();

    private FloatType() {
        super(Type.Category.Float);
    }

    @Override
    public boolean match(Type type) {
        return type.equals(FloatType.type);
    }

    @Override
    public String toString() {
        return "Float";
    }

    public static FloatType instance() {
        return type;
    }


}
