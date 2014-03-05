package cs411.project.pkg2.parser;

import cs411.project.lexer.Token;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Loads the grammar from file, ready to work with.
 * @author Jovanni Cutigni
 */
public class Grammar {
    private static final int[] EMPTY = new int[0];

    private int firstNonterminal;
    private int count;
    Map<String, Integer> ids = new HashMap<String, Integer>();
    Map<Integer, String> names = new HashMap<Integer, String>();
    // Map(id -> rules[rule[token/nonterminal]])
    private ArrayList<ArrayList<int[]>> rules = new ArrayList<ArrayList<int[]>>();

    public static Grammar load(File file) throws IOException {
        Scanner lines = new Scanner(file);
        Grammar g = new Grammar();
        g.firstNonterminal = Token.values().length;
        g.count = g.firstNonterminal - 1;

        // stuff used before it was declared
        Set<Integer> usedUndeclared = new HashSet<Integer>();

        // re-use this
        ArrayList<Integer> rule = new ArrayList<Integer>();

        // read line by line
        while (lines.hasNextLine()) {
            Scanner l = new Scanner(lines.nextLine());

            // if not empty line
            if (l.hasNext()) {
                // must have a name
                String name = l.next();
                // must have a ::=
                if (!l.next().equals("::="))
                    throw new IllegalArgumentException("No ::= after name");

                int id = g.idOf(name);
                g.ensureID(id);

                if (l.hasNext()) {
                    // read an actual production
                    rule.clear();
                    while (l.hasNext()) {
                        String p = l.next();

                        if (p.charAt(0) == '_') {
                            // starts with underscore, should be a terminal Token
                            Token t = Token.valueOf(p);
                            rule.add(t.ordinal());
                        } else {
                            // must be nonterminal
                            int ntid = g.idOf(p);
                            rule.add(ntid);

                            // if not already defined, remember to check later
                            if (g.rulesFor(ntid) == null) {
                                usedUndeclared.add(ntid);
                            }
                        }
                    }

                    int n = rule.size();
                    int[] a = new int[n];
                    for (int i = 0; i < n; i++)
                        a[i] = rule.get(i);

                    g.addRule(id, a);
                } else {
                    // empty string
                    g.addRule(id, EMPTY);
                }
            }
        }
        lines.close();

        // check for nonterminals referenced but never defined
        for (int i : usedUndeclared) {
            if (g.rulesFor(i) == null) {
                throw new IllegalArgumentException("Nonterminal \"\" referenced but never defined");
            }
        }

        return g;
    }

    public List<int[]> rulesFor(int id) {
        return id < rules.size() ? rules.get(id) : null;
    }

    public Integer idOf(String name) {
        Integer value = ids.get(name);
        if (value == null) {
            ++count;
            value = Integer.valueOf(count);
            ids.put(name, value);
            names.put(value, name);
        }
        return value;
    }

    public String nameOf(int id) {
        return names.get(id);
    }

    public boolean isTerminal(int id) {
        return id < firstNonterminal;
    }

    private void ensureID(int id) {
        int n = rules.size();
        if (id >= n) {
            rules.ensureCapacity(id + 1);
            for (int i = n - 1; i < id; i++) {
                rules.add(null);
            }
        }
    }

    private void addRule(int id, int[] list) {
        ensureID(id);

        ArrayList<int[]> ruleList = rules.get(id);
        if (ruleList == null) {
            ruleList = new ArrayList<int[]>();
            rules.set(id, ruleList);
        }

        ruleList.add(list);
    }
}
