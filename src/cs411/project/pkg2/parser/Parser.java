package cs411.project.pkg2.parser;

import java.io.IOException;
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
    private final Grammar grammar;
    private final List<List<Integer>> productions;

    public Parser(SLRTable table, Grammar grammar, int finalReduce) {
        // the table is the identity of the parser
        this.table = table;
        this.finalReduceValue = finalReduce;
        this.grammar = grammar;
        this.productions = grammar.allRules();
    }

    /**
     * Parse a stream of tokens from a lexer
     * @param lexer The lexer interface to pull tokens from
     * @return The output string if accepted, else null
     */
    public List<Integer> parse(LexingStream lexer) throws IllegalArgumentException, IOException {
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
                stack.push(shift);
                // ... and pull the next token and continue
                token = lexer.next();
            } else {
                // if no shift...
                int reduce = table.getReduce(state);
                if (reduce != -1) {
                    // ...and there is a reduce, reduce
                    output.add(reduce);

                    // pop the correct number
                    int reduceCount = table.getReduceCount(state);
                    for (int i = 0; i < reduceCount; i++)
                        stack.pop();

                    // for now current state, goto for that non-terminal
                    state = stack.peek();

                    int nt = productions.get(table.getReduce(state)).get(0);
                    int gotoState = table.getGoto(state, nt);
                    if (gotoState == -1) {
                        throw new IllegalArgumentException("No goto defined for table " + state + " and nonterminal " + nameOrID(nt));
                    }

                    stack.push(gotoState);
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
            return output;
        }

        return null;
    }

    private String nameOrID(int id) {
        String name = grammar.nameOf(id);
        if (name == null)
            name = Integer.toString(id);
        return name;
    }
}
