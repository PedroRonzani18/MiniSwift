package interpreter.type.composed;

import interpreter.type.Type;

public class DictType extends ComposedType {

    private Type keyType;
    private Type valueType;

    private DictType(Type keyType, Type valueType) {
        super(Type.Category.Dict);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    public Type getKeyType() {
        return keyType;
    }

    public Type getValueType() {
        return valueType;
    }

    @Override
    public boolean match(Type type) {
        if (type instanceof DictType) {
            DictType dtype = (DictType) type;
            return this.keyType.equals(dtype.keyType) &&
                        this.valueType.equals(dtype.valueType);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + keyType.hashCode();
        result = prime * result + valueType.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof DictType) {
            return this.match((DictType) obj);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new StringBuffer()
            .append("Dict<")
            .append(keyType)
            .append(",")
            .append(valueType)
            .append(">")
            .toString();
    }

    public static DictType instance(Type keyType, Type valueType) {
        return new DictType(keyType, valueType);
    }

}
