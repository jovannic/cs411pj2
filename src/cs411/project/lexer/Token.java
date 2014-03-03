package cs411.project.lexer;

/**
* @author Jovanni Cutigni
*/
public enum Token {
    _bool, _break, _class, _double,
    _else, _extends, _for, _if,
    _implements, _int, _interface, _newarray,
    _println, _readln, _return, _string,
    _void, _while, _plus, _minus,
    _multiplication, _division, _less, _lessequal,
    _greater, _greaterequal, _equal, _notequal,
    _and, _or, _not, _assignop,
    _semicolon, _comma, _period, _leftparen,
    _rightparen, _leftbracket, _rightbracket, _leftbrace,
    _rightbrace, _boolconstant, _intconstant, _doubleconstant,
    _stringconstant, _id
}
