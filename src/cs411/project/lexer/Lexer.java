package cs411.project.lexer;

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

            CharType cctype = CharType.typeOf(c);
            // switch throws null pointer exception if null
            if (cctype == null) {
                throw new IllegalArgumentException("Unknown character '" + c + "'");
            }

            // branches off the initial character and goes from there
            switch (cctype) {
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
        TrieMap<Token>.TrieSearch f = operators.find(first);

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
        addTo(keywords, Token._bool);
        addTo(keywords, Token._break);
        addTo(keywords, Token._class);
        addTo(keywords, Token._double);
        addTo(keywords, Token._else);
        addTo(keywords, Token._extends);
        addTo(keywords, Token._boolconstant);
        addTo(keywords, Token._for);
        addTo(keywords, Token._if);
        addTo(keywords, Token._implements);
        addTo(keywords, Token._int);
        addTo(keywords, Token._interface);
        addTo(keywords, Token._newarray);
        addTo(keywords, Token._println);
        addTo(keywords, Token._readln);
        addTo(keywords, Token._return);
        addTo(keywords, Token._string);
        addTo(keywords, Token._boolconstant);
        addTo(keywords, Token._void);
        addTo(keywords, Token._while);

        addTo(operators, Token._plus);
        addTo(operators, Token._minus);
        addTo(operators, Token._multiplication);
        addTo(operators, Token._division);
        addTo(operators, Token._greaterequal);
        addTo(operators, Token._greater);
        addTo(operators, Token._lessequal);
        addTo(operators, Token._less);
        addTo(operators, Token._equal);
        addTo(operators, Token._notequal);
        addTo(operators, Token._and);
        addTo(operators, Token._or);
        addTo(operators, Token._not);
        addTo(operators, Token._assignop);
        addTo(operators, Token._semicolon);
        addTo(operators, Token._comma);
        addTo(operators, Token._period);
        addTo(operators, Token._leftparen);
        addTo(operators, Token._rightparen);
        addTo(operators, Token._leftbracket);
        addTo(operators, Token._rightbracket);
        addTo(operators, Token._leftbrace);
        addTo(operators, Token._rightbrace);
    }

    private void addTo(SymbolMap<Token> map, Token t) {
        map.add(t.stringValue(), t);
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
