package interpreter.type.primitive;

import interpreter.type.Type;

public class CharType extends PrimitiveType {

    private static CharType type = new CharType();

    private CharType() {
        super(Type.Category.Char);
    }

    @Override
    public boolean match(Type type) {
        return type.equals(CharType.type);
    }

    @Override
    public String toString() {
        return "Char";
    }

    public static CharType instance() {
        return type;
    }

}
