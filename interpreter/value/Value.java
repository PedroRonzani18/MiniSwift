package interpreter.value;

import java.util.List;
import java.util.Map;

import error.InternalException;
import interpreter.type.Type;

public class Value {
    
    public final Type type;
    public final Object data;

    public Value(Type type, Object data) {
        switch (type.getCategory()) {
            case Bool:
                assert(data instanceof Boolean);
                break;
            case Int:
                assert(data instanceof Integer);
                break;
            case Float:
                assert(data instanceof Float);
                break;
            case Char:
                assert(data instanceof Character);
                break;
            case String:
                assert(data instanceof String);
                break;
            case Array:
                assert(data instanceof List<?>);
                break;
            case Dict:
                assert(data instanceof Map<?,?>);
                break;
            default:
                throw new InternalException("Unrecheable");
        }

        this.type = type;
        this.data = data;
    }

    @Override
    public String toString() {
        return new StringBuffer()
            .append(type)
            .append("(")
            .append(data)
            .append(")")
            .toString();
    }

}
