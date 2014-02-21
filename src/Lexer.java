import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * CS 411 Project 1 : Toy Lexer
 * @author Jovanni Cutigni
 */
public class Lexer {
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

    public static void main(String[] args) throws IOException {
        TrieMap<Token> keywords = new TrieMap<Token>();
        TrieMap<Token> operators = new TrieMap<Token>();

        // load the keywords
        Scanner scanner = new Scanner(new File("keywords.txt"));
        while (scanner.hasNext()) {
            String keyword = scanner.next();
            Token token = Token.valueOf(scanner.next());
            keywords.add(keyword, token);
        }
        scanner.close();

        // load the operators
        ArrayList<String> opsStr = new ArrayList<String>();
        scanner = new Scanner(new File("operators.txt"));
        while (scanner.hasNext()) {
            String keyword = scanner.next();
            Token token = Token.valueOf(scanner.next());
            operators.add(keyword, token);
            opsStr.add("(" + Pattern.quote(keyword) + ")");
        }
        scanner.close();

        Pattern identifier = Pattern.compile("[^\\d\\W]\\w*");
        Pattern integer = Pattern.compile("(\\d+)|(0x[\\dA-Fa-f]+)");
        Pattern real = Pattern.compile("\\d+\\.\\d*([eE][+\\-]?\\d+)?");
        //Pattern ops = Pattern.compile(StringUtils.join(opsStr, "|"));

        scanner = new Scanner(new File("input.txt"));
        while (scanner.hasNext()) {
            if (scanner.hasNext(identifier)) {
                String str = scanner.next(identifier);
                System.out.println(keywords.contains(str) ? keywords.get(str) : Token._id.name());
            } else
                scanner.next();
        }
        scanner.close();

        // Print the symbol table
        keywords.printSwitchTable(80);
        keywords.printSymbolTable(80);
    }
}
