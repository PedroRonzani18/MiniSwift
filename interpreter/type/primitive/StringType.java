package interpreter.type.primitive;

import interpreter.type.Type;

public class StringType extends PrimitiveType {

    private static StringType type = new StringType();

    private StringType() {
        super(Type.Category.String);
    }

    @Override
    public boolean match(Type type) {
        return type.equals(StringType.type);
    }

    @Override
    public String toString() {
        return "String";
    }

    public static StringType instance() {
        return type;
    }

}
