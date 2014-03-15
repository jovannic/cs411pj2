package cs411.project.pkg2.parser;

/**
* Created by Jovanni on 3/14/14.
*/
public interface Lexer {
    /**
     * @return Token id, or -1 for EOF
     */
    int next();
}
