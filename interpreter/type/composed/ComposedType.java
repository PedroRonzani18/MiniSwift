package interpreter.type.composed;

import interpreter.type.Type;
import interpreter.type.TypeException;

public abstract class ComposedType extends Type {

    protected ComposedType(Type.Category classification) {
        super(classification);
    }

    public static ComposedType instance(Type.Category classification, Type ... innerTypes) {
        switch (classification) {
            case Array:
                assert innerTypes.length == 1;
                return ArrayType.instance(classification, innerTypes[0]);
            case Dict:
                assert innerTypes.length == 2;
                return DictType.instance(classification, innerTypes[0], innerTypes[1]);
            default:
                throw new TypeException();
        }
    }

}
