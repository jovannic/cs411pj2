package cs411.project.lexer;

/**
 * @author Jovanni Cutigni
 */
public enum Token {
    _bool("bool"), _break("break"), _class("class"), _double("double"),
    _else("else"), _extends("extends"), _for("for"), _if("if"),
    _implements("implements"), _int("int"), _interface("interface"), _newarray("newarray"),
    _println("println"), _readln("readln"), _return("return"), _string("string"),
    _void("void"), _while("while"), _plus("+"), _minus("-"),
    _multiplication("*"), _division("/"), _less("<"), _lessequal("<="),
    _greater(">"), _greaterequal(">="), _equal("=="), _notequal("!="),
    _and("&&"), _or("||"), _not("!"), _assignop("="),
    _semicolon(";"), _comma(","), _period("."), _leftparen("("),
    _rightparen(")"), _leftbracket("["), _rightbracket("]"), _leftbrace("{"),
    _rightbrace("}"), _boolconstant(null), _intconstant(null), _doubleconstant(null),
    _stringconstant(null), _id(null);

    private final static Token[] fromOrdinal = Token.values();
    public static final int length = fromOrdinal.length;

    private final String value;

    private Token(String value) {
        this.value = value;
    }

    public String stringValue() {
        return value;
    }

    public static Token valueOf(int ordinal) {
        return fromOrdinal[ordinal];
    }
}
