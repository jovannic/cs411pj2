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
    private int nextNonterminal;

    Map<String, Integer> ids = new HashMap<String, Integer>();
    Map<Integer, String> names = new HashMap<Integer, String>();

    // rulseFor[nonterminalID] -> List of production rules
    private ArrayList<ArrayList<int[]>> rulesFor = new ArrayList<ArrayList<int[]>>();

    public static Grammar load(File file) throws IOException {
        Scanner lines = new Scanner(file);
        Grammar g = new Grammar();

        g.firstNonterminal = Token.values().length;
        g.nextNonterminal = g.firstNonterminal;

        // stuff used before it was declared
        Set<Integer> usedBeforeDefined = new HashSet<Integer>();

        // re-use this
        int[] ruleTemp = new int[128];

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
                g.ensureIndex(id);

                int ruleLength = 2;
                ruleTemp[0] = id;
                ruleTemp[1] = 0;

                if (l.hasNext()) {
                    // read an actual production
                    while (l.hasNext()) {
                        String p = l.next();

                        if (p.charAt(0) == '_') {
                            // starts with underscore, should be a terminal Token
                            Token t = Token.valueOf(p);
                            ruleTemp[ruleLength++] = t.ordinal();
                        } else {
                            // must be nonterminal
                            int ntid = g.idOf(p);
                            ruleTemp[ruleLength++] = ntid;

                            // if not already defined, remember to check later
                            if (g.rulesFor(ntid) == null) {
                                usedBeforeDefined.add(ntid);
                            }
                        }
                    }
                } else {
                    // empty string
                }

                g.addRule(id, Arrays.copyOf(ruleTemp, ruleLength));
            }
        }
        lines.close();

        // check for nonterminals referenced but never defined
        for (int i : usedBeforeDefined) {
            if (g.rulesFor(i) == null) {
                throw new IllegalArgumentException("Nonterminal \"" + g.nameOf(i) + "\" referenced but never defined");
            }
        }

        return g;
    }

    public List<int[]> rulesFor(int nonterminalID) {
        nonterminalID -= firstNonterminal;
        return nonterminalID < rulesFor.size() ? rulesFor.get(nonterminalID) : null;
    }

    public Integer idOf(String name) {
        Integer id = ids.get(name);
        if (id == null) {
            id = Integer.valueOf(nextNonterminal);
            nextNonterminal++;
            ids.put(name, id);
            names.put(id, name);
        }
        return id;
    }

    public String nameOf(int nonterminalID) {
        return names.get(nonterminalID);
    }

    public boolean isTerminal(int id) {
        return id < firstNonterminal;
    }

    private void ensureIndex(int id) {
        int length = rulesFor.size();
        if (id >= length) {
            int neededLength = id + 1;
            rulesFor.ensureCapacity(neededLength);
            for (int i = length; i < neededLength; i++) {
                rulesFor.add(null);
            }
        }
    }

    private void addRule(int id, int[] rule) {
        id -= firstNonterminal;

        ensureIndex(id);

        ArrayList<int[]> ruleList = rulesFor.get(id);
        if (ruleList == null) {
            ruleList = new ArrayList<int[]>();
            rulesFor.set(id, ruleList);
        }

        ruleList.add(rule);
    }
}
