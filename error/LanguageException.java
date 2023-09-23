package error;

public class LanguageException extends RuntimeException {

    private int line;

    public static enum Error {
        InvalidLexeme("Lexema inválido [%s]", 1),
        UnexpectedEOF("Fim de arquivo inesperado", 0),
        UnexpectedLexeme("Lexema não esperado [%s]", 1),
        UndeclaredVariable("Variável não declarada [%s]", 1),
        AlreadyDeclaredVariable("Variável já declarada anteriormente [%s]", 1),
        UnitializedVariable("Variável não inicializada [%s]", 1),
        ConstantAssignment("Atribuição em variável constante [%s]", 1),
        InvalidType("Tipo inválido [%s]", 1),
        InvalidOperation("Operação inválida", 0);

        public final String msg;
        public final int args;

        private Error(String msg, int args) {
            this.msg = msg;
            this.args = args;
        }
    }

    protected LanguageException(int line, String msg) {
        super(String.format("%02d: %s", line, msg));
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    public static LanguageException instance(int line, Error error, String ... args) {
        assert error.args == args.length;
        String msg = error.args == 0 ? error.msg : String.format(error.msg, (Object[]) args);
        return new LanguageException(line, msg);
    }

}
