package cs411.project.lexer;

import java.io.IOException;

/**
 * Created by Jovanni on 2/20/14.
 */
public interface CharStream {
    boolean hasNext() throws IOException;
    char next() throws IOException;
    char peek() throws IOException;
    void pop() throws IOException;
}
