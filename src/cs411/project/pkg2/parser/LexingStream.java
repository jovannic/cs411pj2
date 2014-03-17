package cs411.project.pkg2.parser;

import java.io.IOException;

/**
* Created by Jovanni on 3/14/14.
*/
public interface LexingStream {
    /**
     * @return Token id, or -1 for EOF
     */
    int next() throws IOException;
}
