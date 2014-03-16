package cs411.project.pkg2.parser;

import cs411.project.lexer.CharStream;
import cs411.project.lexer.Lexer;
import cs411.project.lexer.Token;
import cs411.project.pkg2.parser.LexingStream;

import java.io.IOException;

/**
 * @author Jovanni Cutigni
 */
public class JovanniLexingStream implements LexingStream {
    Lexer lexer;
    CharStream stream;

    public JovanniLexingStream(Lexer lexer, CharStream stream) {
        this.lexer = lexer;
        this.stream = stream;
    }

    @Override
    public int next() {
        try {
            Token t = lexer.next(stream);
            return t != null ? t.ordinal() : -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
