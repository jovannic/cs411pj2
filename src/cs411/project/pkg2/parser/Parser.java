package cs411.project.pkg2.parser;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * @author Jovanni Cutigni
 */
public class Parser {
    private final SLRTable table;
    private final int finalReduceValue;

    public Parser(SLRTable table, int finalReduce) {
        // the table is the identity of the parser
        this.table = table;
        this.finalReduceValue = finalReduce;
    }

    public boolean parse(Lexer lexer) {
        Deque<Integer> stack = new ArrayDeque<Integer>();
        List<Integer> output = new ArrayList<Integer>();

        // initial state
        stack.push(0);

        // the next lexer token
        int token;

        // while not eof, do parser action
        token = lexer.next();
        while (token != -1) {
            // current state
            int state = stack.peek();

            int shift = table.getShift(state, token);
            if (shift != -1) {
                // if there's a shift action, goto...
                stack.push(table.getGoto(state, token));
                // ... and pull the next token and continue
                token = lexer.next();
            } else {
                // if no shift...
                int reduce = table.getReduce(state);
                if (reduce != -1) {
                    // ...and there is a reduce, reduce
                    output.add(reduce);

                    // pop the correct number
                    int reduceCount = 1; // TODO: make number to shift available to parser
                    for (int i = 0; i < reduceCount; i++)
                        stack.pop();

                    // nonterminal we're reducing to
                    int left = 0; // TODO: make nonterminal we're reducing to available to parser

                    // for now current state, goto for that non-terminal
                    state = stack.peek();
                    stack.push(table.getGoto(state, left));
                } else {
                    // no action defined, error
                    throw new IllegalArgumentException("No action defined for token");
                }
            }
        }

        // end of file, final step:
        int state = stack.peek();
        int reduce = table.getReduce(state);
        if (reduce == finalReduceValue) {
            //accept
            return true;
        }

        return false;
    }
}
