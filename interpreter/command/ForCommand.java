package interpreter.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import error.LanguageException;
import interpreter.expr.Expr;
import interpreter.expr.Variable;
import interpreter.type.Type;
import interpreter.type.Type.Category;
import interpreter.value.Value;

public class ForCommand extends Command {
    private Variable variable;
    private Expr expr;
    private Command cmds;

    public ForCommand(int line, Variable variable, Expr expr, Command cmds) {
        super(line);
        this.variable = variable;
        this.expr = expr;
        this.cmds = cmds;
    }

    @Override
    public void execute() {

        List<Category> allowedCategories = Arrays.asList(Category.Array, Category.String);

        Value exprValue = expr.expr();

        if (!allowedCategories.contains(exprValue.type.getCategory()))
            throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);

        Type varType = variable.getType();

        switch (exprValue.type.getCategory()) {

            case String:

                if (!varType.getCategory().equals(Category.Char))
                    throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidType,
                            varType.toString());

                String dataString = (String) exprValue.data;
                for (char c : dataString.toCharArray()) {
                    if(variable.isConstant())
                        variable.initialize(new Value(varType, c));
                    else 
                        variable.setValue(new Value(varType, c));
                    cmds.execute();
                }   
                     
                break;

            case Array:

                List<Object> listData = (ArrayList<Object>) exprValue.data;
                
                for (Object it : listData) {
                    if(variable.isConstant())
                        variable.initialize(new Value(varType, it));
                    else 
                        variable.setValue(new Value(varType, it));
                    cmds.execute();
                }      

                break;

            default:
                throw LanguageException.instance(super.getLine(), LanguageException.Error.InvalidOperation);
        }
    }
}
