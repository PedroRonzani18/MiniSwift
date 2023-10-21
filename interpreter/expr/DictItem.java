package interpreter.expr;

public class DictItem {
    public Expr key;
    public Expr value;

    public DictItem(Expr key, Expr value) {
        this.key = key;
        this.value = value;
    }
}