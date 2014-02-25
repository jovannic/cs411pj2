import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * CS 411 Project 1 : Toy Lexer
 * @author Jovanni Cutigni
 */
public class Lexer {
    SymbolMap<Token> keywords = new HashSymbolMap<Token>();
    TrieMap<Token> operators = new TrieMap<Token>();

    public Lexer() {
        loadTablesExplictly();
    }

    /**
     * If called each time before next, can preserve whitespace when tokenizing
     * @param stream The stream to read from
     * @return The whitespace up to before the next non-whitespace character
     */
    public String nextWhitespace(CharStream stream) throws IOException {
        StringBuilder b = new StringBuilder();
        while (stream.hasNext() && CharType.is(stream.peek(), CharType.SPACE))
            b.append(stream.next());
        return b.toString();
    }

    /**
     * Reads one token from the stream if it can
     * @param stream The stream to read the token from
     * @return The next token, or null if end of stream
     * @throws IOException If an IO error occurs when reading from the stream
     * @throws IllegalArgumentException If couldn't parse correctly
     */
    public Token next(CharStream stream) throws IOException, IllegalArgumentException {
        while (stream.hasNext()) {
            char c = stream.next();

            // branches off the initial character and goes from there
            switch (CharType.typeOf(c)) {
                case SPACE:
                    break; // ignore whitespace, continue
                case SYMBOL:
                    // collect the characters
                    StringBuilder b = new StringBuilder();
                    b.append(c);
                    consumeSymbol(stream, b);
                    // check if it's a keyword, otherwise id
                    String id = b.toString();
                    Token keyword = keywords.get(id);
                    return keyword != null ? keyword : Token._id;
                case OP:
                    return parseOp(stream, c);
                case SLASH:
                    // the slash is an operator and also begins comments
                    //  so we have to look forward to get context
                    if (stream.hasNext()) {
                        c = stream.peek();

                        if (c == '*') {
                            stream.pop();
                            // block comment
                            ignoreUntil(stream, "*/");
                            break;
                        } else if (c == '/') {
                            stream.pop();
                            // line comment
                            ignoreUntil(stream, "\n");
                            break;
                        }
                    }
                    // must be part of an operator
                    return parseOp(stream, '/');
                case DIGIT:
                    b = new StringBuilder();
                    // might be hex, look ahead
                    if (c == '0' && stream.hasNext() && Character.toLowerCase(stream.peek()) == 'x') {
                        stream.pop(); // pop x
                        // consume digits AND letters:
                        //  a 'hex' string with an _ or Z in it is an error
                        consumeSymbol(stream, b);
                        long value = Long.valueOf(b.toString(), 16);
                    } else {
                        b.append(c);
                        // consume digits and letters again
                        consumeSymbol(stream, b);

                        // if there's a '.' it's double
                        if (stream.hasNext() && stream.peek() == '.') {
                            // argh, double value, handle that
                            b.append(stream.next());

                            while (stream.hasNext() && CharType.isSymbol(c = stream.peek()) && Character.toLowerCase(c) != 'e') {
                                b.append(c);
                                stream.pop();
                            }

                            if (stream.hasNext() && Character.toLowerCase(c) == 'e') {
                                b.append(c);
                                stream.pop();

                                // see if sign
                                c = stream.peek();
                                if (c == '+' || c == '-') {
                                    b.append(c);
                                    stream.pop();
                                }

                                // consume the integer exponent
                                consumeSymbol(stream, b);
                            }

                            double doubleValue = Double.valueOf(b.toString());
                            return Token._doubleconstant;
                        }

                        // otherwise plain ol' integer value
                        long value = Long.valueOf(b.toString());
                    }
                    return Token._intconstant;
                case QUOTE:
                    String str = parseString(stream);
                    return Token._stringconstant;
                default:
                    throw new IllegalArgumentException("Unknown character: '" + c + "'");
            }
        }
        return null;
    }

    private void consumeSymbol(CharStream stream, StringBuilder b) throws IOException {
        char c;
        while (stream.hasNext() && CharType.isSymbol(c = stream.peek())) {
            b.append(c);
            stream.pop();
        }
    }

    // use behavior of the trie to parse messes of operators
    private Token parseOp(CharStream stream, char first) throws IOException {
        StringBuilder b = new StringBuilder();
        b.append(first);
        TrieMap<Token>.Finder f = operators.find(first);

        char c;
        while (!f.hasEnded() && stream.hasNext() && CharType.is(c = stream.peek(), CharType.OP) && f.hasNext(c)) {
            c = stream.next();
            f.append(c);
            b.append(c);
        }
        f.end();

        if (!f.isMatch())
            throw new IllegalArgumentException("Invalid operator: " + b);

        return f.getData();
    }

    private void loadTablesFromFile() throws FileNotFoundException {
        // load the keywords
        Scanner scanner = new Scanner(new File("keywords.txt"));
        while (scanner.hasNext()) {
            String keyword = scanner.next();
            Token token = Token.valueOf(scanner.next());
            keywords.add(keyword, token);
        }
        scanner.close();

        // load the operators
        scanner = new Scanner(new File("operators.txt"));
        while (scanner.hasNext()) {
            String keyword = scanner.next();
            Token token = Token.valueOf(scanner.next());
            operators.add(keyword, token);
        }
        scanner.close();
    }

    private void loadTablesExplictly() {
        keywords.add("bool", Token._bool);
        keywords.add("break", Token._break);
        keywords.add("class", Token._class);
        keywords.add("double", Token._double);
        keywords.add("else", Token._else);
        keywords.add("extends", Token._extends);
        keywords.add("false", Token._boolconstant);
        keywords.add("for", Token._for);
        keywords.add("if", Token._if);
        keywords.add("implements", Token._implements);
        keywords.add("int", Token._int);
        keywords.add("interface", Token._interface);
        keywords.add("newarray", Token._newarray);
        keywords.add("println", Token._println);
        keywords.add("readln", Token._readln);
        keywords.add("return", Token._return);
        keywords.add("string", Token._string);
        keywords.add("true", Token._boolconstant);
        keywords.add("void", Token._void);
        keywords.add("while", Token._while);
        // IMPORTANT: for common prefixes they're prioritized in order added
        operators.add("+", Token._plus);
        operators.add("-", Token._minus);
        operators.add("*", Token._multiplication);
        operators.add("/", Token._division);
        operators.add("<=", Token._greaterequal);
        operators.add("<", Token._greater);
        operators.add(">=", Token._lessequal);
        operators.add(">", Token._less);
        operators.add("==", Token._equal);
        operators.add("!=", Token._notequal);
        operators.add("&&", Token._and);
        operators.add("||", Token._or);
        operators.add("!", Token._not);
        operators.add("=", Token._assignop);
        operators.add(";", Token._semicolon);
        operators.add(",", Token._comma);
        operators.add(".", Token._period);
        operators.add("(", Token._leftparen);
        operators.add(")", Token._rightparen);
        operators.add("[", Token._leftbracket);
        operators.add("]", Token._rightbracket);
        operators.add("{", Token._leftbrace);
        operators.add("}", Token._rightbrace);
    }

    // ignores until eof or end of match found. assumes str.length() > 0
    private static boolean ignoreUntil(CharStream stream, String str) throws IOException {
        int n = str.length();
        int matched = 0;
        char next = str.charAt(0);
        // consume until match found
        while (stream.hasNext()) {
            char c = stream.next();
            if (c == next) {
                ++matched;
                if (matched < n)
                    next = str.charAt(matched);
                else return true;
            }
        }
        return false;
    }

    // collects until " found, then returns the string
    private static String parseString(CharStream stream) throws IOException {
        StringBuilder b = new StringBuilder();
        while (stream.hasNext()) {
            char c = stream.next();
            if (c == '\"') {
                break;
            } else {
                b.append(c);
            }
        }
        return b.toString();
    }

    public static void main(String[] args) throws IOException {
        Lexer lex = new Lexer();

        BasicFileStream stream = new BasicFileStream("input.txt");
        while (stream.hasNext()) {
            System.out.print(lex.nextWhitespace(stream));
            System.out.print(lex.next(stream));
        }
        stream.close();
    }
}
