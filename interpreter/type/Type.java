package interpreter.type;

public abstract class Type {

    public static enum Category {
        Bool,
        Int,
        Float,
        Char,
        String,
        Array,
        Dict
    }

    private Category category;

    protected Type(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public abstract boolean match(Type type);

}
