package interpreter.expr;

import java.util.ArrayList;
import java.util.List;

import error.LanguageException;
import interpreter.type.composed.ArrayType;
import interpreter.value.Value;

public class ArrayExpr extends Expr {
    private ArrayType type;
    private List<Expr> items;

    public ArrayExpr(int line, ArrayType type, List<Expr> items) {
        super(line);
        this.type = type;
        this.items = items;        
    }

    @Override
    public Value expr() {

        List<Object> returnItems = new ArrayList<>();
        
        for (Expr expr : items) {

            Value exprValue = expr.expr();
            if(!exprValue.type.getCategory().equals(type.getInnerType().getCategory()))
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            exprValue.type.toString());
                            
            returnItems.add(exprValue.data);
        }

        return new Value(type, returnItems);
    }
}
