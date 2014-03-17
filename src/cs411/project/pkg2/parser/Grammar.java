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
    private int firstNonterminal;
    private int nextNonterminal;

    Map<String, Integer> ids = new HashMap<String, Integer>();
    Map<Integer, String> names = new HashMap<Integer, String>();

    // rulseFor[nonterminalID] -> List of production rules
    private ArrayList<ArrayList<List<Integer>>> rulesFor = new ArrayList<ArrayList<List<Integer>>>();
    private ArrayList<List<Integer>> rules = new ArrayList<List<Integer>>();

    public static Grammar load(File file) throws IOException {
        Scanner lines = new Scanner(file);
        Grammar g = new Grammar();

        g.firstNonterminal = Token.length;
        g.nextNonterminal = g.firstNonterminal;

        // stuff used before it was declared
        Set<Integer> usedBeforeDefined = new HashSet<Integer>();

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

                ArrayList<Integer> ruleTemp = new ArrayList<Integer>();
                ruleTemp.add(id);
                ruleTemp.add(Table.DOT);

                if (l.hasNext()) {
                    // read an actual production
                    while (l.hasNext()) {
                        String p = l.next();

                        if (p.charAt(0) == '_') {
                            // starts with underscore, should be a terminal Token
                            Token t = Token.valueOf(p);
                            ruleTemp.add(t.ordinal());
                        } else {
                            // must be nonterminal
                            int ntid = g.idOf(p);
                            ruleTemp.add(ntid);

                            // if not already defined, remember to check later
                            if (g.rulesFor(ntid) == null) {
                                usedBeforeDefined.add(ntid);
                            }
                        }
                    }
                } else {
                    // empty string
                }

                g.addRule(id, ruleTemp);
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

    public List<List<Integer>> rulesFor(int nonterminalID) {
        nonterminalID -= firstNonterminal;
        return nonterminalID < rulesFor.size() ? rulesFor.get(nonterminalID) : null;
    }

    public List<List<Integer>> allRules() {
        return Collections.unmodifiableList(rules);
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

    public String nameOf(int id) {
        if (isTerminal(id)) {
            return Token.valueOf(id).toString();
        }
        return names.get(id);
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

    private void addRule(int id, List<Integer> rule) {
        id -= firstNonterminal;

        ensureIndex(id);

        ArrayList<List<Integer>> ruleList = rulesFor.get(id);
        if (ruleList == null) {
            ruleList = new ArrayList<List<Integer>>();
            rulesFor.set(id, ruleList);
        }

        // list indexed by nonterminal
        ruleList.add(rule);
        // list of all production
        rules.add(rule);
    }

    public int getFirstNonterminal() {
        return firstNonterminal;
    }
}
