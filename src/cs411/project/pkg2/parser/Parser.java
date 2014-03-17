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
    private final Grammar grammar;

    public Parser(SLRTable table, Grammar grammar) {
        // the table is the identity of the parser
        this.table = table;
        this.grammar = grammar;
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

        // the first/next lexer token
        int token = lexer.next();
        System.out.print(grammar.nameOrIdOf(token) + ": ");

        // while not eof, do parser action
        while (token != -1) {
            // current state
            int state = stack.peek();

            int shift = table.getShift(state, token);
            if (shift != -1) {
                // if there's a shift action, goto...
                stack.push(shift);
                System.out.println("shift ");

                // ... and pull the next token and continue
                token = lexer.next();
                System.out.print(grammar.nameOrIdOf(token) + ": ");
            } else {
                // if no shift...
                int reduce = table.getReduce(state);
                if (reduce != -1) {
                    // ...and there is a reduce, reduce
                    output.add(reduce);

                    int left = grammar.nonterminalForRule(reduce);

                    System.out.print("r" + reduce + "[" + grammar.nameOrIdOf(left) + "] ");

                    // pop the correct number
                    int reduceCount = table.getReduceCount(state);
                    for (int i = 0; i < reduceCount; i++)
                        stack.pop();

                    // for new current state, goto for that non-terminal
                    state = stack.peek();

                    //int nt = grammar.nonterminalForRule(table.getReduce(state));
                    int gotoState = table.getGoto(state, left);
                    if (gotoState == -1) {
                        System.out.println();
                        throw new IllegalArgumentException("No goto defined in table " + state
                                + " for " + grammar.nameOrIdOf(left));
                    }

                    stack.push(gotoState);
                } else {
                    System.out.println();
                    // no action defined, error
                    throw new IllegalArgumentException("No action defined in table " + state
                            + " for " + grammar.nameOrIdOf(token));
                }
            }
        }

        // end of file, final step:
        int state = stack.peek();
        int reduce = table.getReduce(state);
        int left = grammar.nonterminalForRule(reduce);
        if (left == grammar.intialNonterminal()) {
            //accept
            return output;
        }

        return null;
    }
}
