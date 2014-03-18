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

    private final LRTable table;
    private final Grammar grammar;

    public Parser(LRTable table, Grammar grammar) {
        // the table is the identity of the parser
        this.table = table;
        this.grammar = grammar;
    }

    /**
     * Parse a stream of tokens from a lexer
     *
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
        while (true) {
            // current state
            int state = stack.peek();

            int shift = table.getShift(state, token);
            if (shift != -1) {
                // if there's a shift defined, it's the next state
                stack.push(shift);
                System.out.println("shift ");

                token = lexer.next();

                System.out.print(grammar.nameOrIdOf(token) + ": ");
            } else {
                // if no shift...
                int reduce = table.getReduce(state, token);
                if (reduce != -1) {
                    // if there's a reduce defined, it's the rule to reduce with
                    output.add(reduce);

                    int left = grammar.nonterminalForRule(reduce);

                    System.out.print("r" + reduce + "[" + grammar.nameOrIdOf(left) + "] ");

                    // if EOF and reduced to the initial noterminal, accept
                    if (token == -1 && left == grammar.intialNonterminal()) {
                        System.out.println("\n[Accept]");
                        return output;
                    }

                    // pop the rule off the stack
                    int reduceCount = table.getReduceCount(state, token);
                    for (int i = 0; i < reduceCount; i++) {
                        stack.pop();
                    }
                    state = stack.peek();

                    // goto the next state
                    int gotoState = table.getGoto(state, left);
                    if (gotoState != -1) {
                        stack.push(gotoState);
                    } else {
                        System.out.println();
                        throw new IllegalArgumentException("No goto defined in table " + state
                                + " for " + grammar.nameOrIdOf(left));
                    }
                } else {
                    System.out.println();
                    // no action defined, error
                    throw new IllegalArgumentException("No action defined in table " + state
                            + " for " + grammar.nameOrIdOf(token));
                }
            }
        }
    }
}
