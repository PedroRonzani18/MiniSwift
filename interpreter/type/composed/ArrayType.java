package interpreter.type.composed;

import interpreter.type.Type;

public class ArrayType extends ComposedType {

    private Type innerType;

    private ArrayType(Type innerType) {
        super(Type.Category.Array);
        this.innerType = innerType;
    }

    public Type getInnerType() {
        return innerType;
    }

    @Override
    public boolean match(Type type) {
        if (type instanceof ArrayType) {
            ArrayType atype = (ArrayType) type;
            return this.innerType.equals(atype.innerType);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 17 + this.innerType.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof ArrayType) {
            return this.match((ArrayType) obj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new StringBuffer()
            .append("Array<")
            .append(innerType)
            .append(">")
            .toString();
    }

    public static ArrayType instance(Type innerType) {
        return new ArrayType(innerType);
    }

}
