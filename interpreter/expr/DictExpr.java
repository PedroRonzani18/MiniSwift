package interpreter.expr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import error.LanguageException;
import interpreter.type.composed.DictType;
import interpreter.value.Value;

public class DictExpr extends Expr {

    private DictType type;
    private List<DictItem> items;

    public DictExpr(int line, DictType type, List<DictItem> items) {
        super(line);
        this.type = type;
        this.items = items;
    }

    @Override
    public Value expr() {

        Map<Object, Object> returnItems = new HashMap<>();

        for (DictItem dictItem : items) {

            Value keyValue = dictItem.key.expr();
            Value valueValue = dictItem.value.expr();

            if (!keyValue.type.getCategory().equals(type.getKeyType().getCategory()))
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        keyValue.type.toString());

            if (!valueValue.type.getCategory().equals(type.getValueType().getCategory()))
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                        valueValue.type.toString());

            returnItems.put(keyValue.data, valueValue.data);
        }

        return new Value(type, returnItems);
    }
}
