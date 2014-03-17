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
    private boolean usesToken = false;

    Map<String, Integer> ids = new HashMap<String, Integer>();
    Map<Integer, String> names = new HashMap<Integer, String>();

    // rulseFor[nonterminalID] -> List of production rules
    private ArrayList<ArrayList<List<Integer>>> rulesFor;
    private List<List<Integer>> rules;

    public static Grammar load(File file) throws IOException {
        return new Grammar(file);
    }

    public Grammar(File file) throws IOException {
        Scanner lines = new Scanner(file);

        usesToken = true;
        firstNonterminal = Token.length;
        nextNonterminal = firstNonterminal;

        rulesFor = new ArrayList<ArrayList<List<Integer>>>();
        rules = new ArrayList<List<Integer>>();

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

                int id = ensureIdOf(name);
                ensureIndex(id);

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
                            int ntid = ensureIdOf(p);
                            ruleTemp.add(ntid);

                            // if not already defined, remember to check later
                            if (rulesFor(ntid) == null) {
                                usedBeforeDefined.add(ntid);
                            }
                        }
                    }
                } else {
                    // empty string
                }

                addRuleTo(id, ruleTemp);
                rules.add(ruleTemp);
            }
        }
        lines.close();

        // check for nonterminals referenced but never defined
        for (int i : usedBeforeDefined) {
            if (rulesFor(i) == null) {
                throw new IllegalArgumentException("Nonterminal \"" + nameOf(i) + "\" referenced but never defined");
            }
        }
    }

    public Grammar(List<List<Integer>> rules, int firstNonterminal) {
        usesToken = false;
        this.firstNonterminal = firstNonterminal;
        nextNonterminal = firstNonterminal;

        this.rules = rules;
        rulesFor = new ArrayList<ArrayList<List<Integer>>>();

        for (List<Integer> rule : rules) {
            addRuleTo(rule.get(0), rule);
        }
    }

    public List<List<Integer>> rulesFor(int nonterminalID) {
        nonterminalID -= firstNonterminal;
        return nonterminalID < rulesFor.size() ? rulesFor.get(nonterminalID) : null;
    }

    public List<List<Integer>> allRules() {
        return Collections.unmodifiableList(rules);
    }

    private Integer ensureIdOf(String name) {
        Integer id = ids.get(name);
        if (id == null) {
            id = Integer.valueOf(nextNonterminal);
            nextNonterminal++;
            ids.put(name, id);
            names.put(id, name);
        }
        return id;
    }

    /**
     * @return id for the name, or -1
     */
    public int idOf(String name) {
        Integer id = ids.get(name);
        return id != null ? id : -1;
    }

    /**
     * @return
     */
    public String nameOf(int id) {
        if (usesToken && isTerminal(id)) {
            return Token.valueOf(id).toString();
        }
        return names.get(id);
    }

    public String nameOrIdOf(int id) {
        String name = nameOf(id);
        if (name == null)
            name = Integer.toString(id);
        return name;
    }

    public boolean isTerminal(int id) {
        return id >= 0 && id < firstNonterminal;
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

    private void addRuleTo(int nonterminal, List<Integer> rule) {
        int id = nonterminal - firstNonterminal;

        ensureIndex(id);

        ArrayList<List<Integer>> ruleList = rulesFor.get(id);
        if (ruleList == null) {
            ruleList = new ArrayList<List<Integer>>();
            rulesFor.set(id, ruleList);
        }

        ruleList.add(rule);
    }


    public static List<Integer> makeRule(int nonterminal, int... rule) {
        ArrayList<Integer> r = new ArrayList<Integer>(2 + rule.length);
        r.add(nonterminal);
        r.add(Table.DOT);

        int n = rule.length;
        for (int i = 0; i < n; i++) {
            r.add(rule[i]);
        }
        return r;
    }

    public static List<Integer> makeEmpty(int nonterminal) {
        ArrayList<Integer> r = new ArrayList<Integer>(2);
        r.add(nonterminal);
        r.add(Table.DOT);
        return r;
    }
}
