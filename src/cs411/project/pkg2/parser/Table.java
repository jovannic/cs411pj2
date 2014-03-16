/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import java.util.LinkedList;
import java.util.*;

/**
 *
 * @author Michael
 */
public class Table {

    private int nonterminal = 0;  // this is where our nonterminals will begin
    private static final Integer DOT = 0; // the value of our dot
    private static final Integer AFTER_DOT = 2;
    private LinkedList<LinkedList<List<Integer>>> listomania;
    //this should not change, so it should be final
    private final LinkedList<LinkedList<Integer>> productions;
    private SLRTable table;

    public Table(LinkedList<LinkedList<Integer>> productions) {
        this.productions = productions;

        listomania = new LinkedList();
        table = new HashSLRTable();
    }

    /**
     * This constructor is for testing purposes
     *
     * @param productions a list of productions
     * @param nonterminal A value for when terminals stop and nonterminals begin
     * or vise versa
     */
    public Table(LinkedList<LinkedList<Integer>> productions, int nonterminal) {
        this(productions);
        this.nonterminal = nonterminal;
    }

    /**
     * This is a method for testing, so we can see the output of the tables.
     *
     * @return
     */
    public LinkedList<LinkedList<List<Integer>>> getLists() {
        return listomania;
    }

    public void makeAi() {
        //int tableNum = 0;
        int startProductionNum = 0;
        //this adds our first rule to the table
        LinkedList<Integer> rule = productions.get(startProductionNum);
        LinkedList<LinkedList<Integer>> ruleSq = new LinkedList();
        ruleSq.add(rule);
        listomania.add((LinkedList) ruleSq.clone());

        //if we have nonterminals after the dot, we want to add all of their rules to the table
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            addNonterminalsToTable(tableNum);
            generateTables(tableNum);
            
        }
    }

    private void generateTables(int tableNum) {
        Integer leadingCharacter; //can be nonterminal or terminal, like cancer
        LinkedList<LinkedList<Integer>> ruleList = new LinkedList(); //its called characterList but it is Integers...

        // linked list index lookups aren't cheap, reuse
        LinkedList<List<Integer>> table = listomania.get(tableNum);

        //for each of the rules in X
        
        for (int ruleNum = 0; ruleNum < table.size(); ruleNum++) {
            ruleList = new LinkedList();
            List<Integer> rule = table.get(ruleNum);

            int charNum = findAfterDot(rule);

            if (charNum < rule.size()) {
                leadingCharacter = rule.get(charNum);
            } else {
                leadingCharacter = Integer.MIN_VALUE;
            }
            if (leadingCharacter.intValue() == Integer.MIN_VALUE) {
                //get the first character
                Integer production = rule.get(0);
                int right = 0; // TODO: acctual value
                addReduce(tableNum, production, right);
                return;
            }

            //add all of the rules that match this character
            for (int i = 0; i < table.size(); i++) {
                charNum = findAfterDot(table.get(i));
                //if it is the same as our leading character
                if (charNum < table.get(i).size()) {
                    if (table.get(i).get(charNum).intValue() == leadingCharacter.intValue()) {
                        //add it to the list
                        ruleList.add(new LinkedList(table.get(i)));
                        //we need to clone because we want to be able to manipulate these rules.
                    }
                }
                //we need to shift all of the rules
                // that means move the zero over one spot
            }
            for (LinkedList<Integer> ruleListItem : ruleList) {
                int afterDotIndex = findAfterDot(ruleListItem);
                
                int dotIndex = afterDotIndex - 1;

                //make sure we are not out of bounds
                if (afterDotIndex < ruleListItem.size()) {
                    //remove dot
                    ruleListItem.remove(dotIndex);
                    //add the dot to its pervious position +1
                    ruleListItem.add(afterDotIndex, DOT);
                }
                //we do this for every rule in the list
            }
            // linear search for tables that BEGIN WITH the production rules of the table we want to add
            int gotoTable = doesTableExist(ruleList);
            if (gotoTable == -1) {
                //make table
                //add it to the end of the linked list
                int tableIndex = tableNum;
                int symbol = leadingCharacter;
                int next = listomania.size();
                //parsetable.shiftAdd(tableIndex, symbol, next);
                //tableNum = listomania.size();
                gotoTable = listomania.size();
                listomania.add((LinkedList) ruleList);
                

            } else {
                //do nothing... maybe
            }

            if (isTerminal(leadingCharacter)) {
                //we want to add it to the shift table
                addShift(tableNum, leadingCharacter, gotoTable);
            } else {
                // for nonterminals on top of the stack (they arent really added to the stack)
                // we want to add them to the goto
                
                addGoto(tableNum, leadingCharacter, gotoTable);
            }
        }
    }

    /**
     * Try to find a table with the matching initial rules
     *
     * @param query
     * @return Returns an int corresponding to the table that has matching
     * initial rules, or -1 if a table was not found
     */
    private int doesTableExist(LinkedList<LinkedList<Integer>> query) {
        //int tableNum = 0;
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            LinkedList<List<Integer>> table = listomania.get(tableNum);

            boolean flag = true;
            for (int i = 0; i < query.size(); i++) {
                List<Integer> queryItem = query.get(i);
                List<Integer> tableItem = table.get(i);

                for (int j = 0; j < queryItem.size(); j++) {
                    Integer a = tableItem.get(j);
                    Integer b = queryItem.get(j);
                    if (a.intValue() != b.intValue()) {
                        flag = false;
                        break;
                    }
                }
                if (flag == false) {
                    break;
                }
            }
            if (flag == true) {
                //if we finish checking our query, and we still have true, then return the table we are on.
                return tableNum;
            }
        }

        return -1;
    }

    private int findAfterDot(List<Integer> rule) {
        // we want to find the value of J where we see 0, or rather our dot
        int dotIndex = rule.indexOf(DOT);

        // add 1 again to see the information after the dot
        return dotIndex != -1 ? dotIndex + 1 : 1;
        // not sure what to do if not found
    }

    private boolean isTerminal(int number) {
        return number < nonterminal;
    }

    private boolean isNonTerminal(int number) {
        return !isTerminal(number);
    }

    private void addNonterminalsToTable(int tableNum) {
        //int tableNum = 0;
        //int ruleNum = 0;
        LinkedList<List<Integer>> table = listomania.get(tableNum);
        for (int ruleNum = 0; ruleNum < table.size(); ruleNum++) {
            List<Integer> rule = table.get(ruleNum);

            //find the point after the dot dot
            int charNum = findAfterDot(rule);

            //get the character after the dot
            if (charNum < rule.size()) {
                Integer productionPointer = rule.get(charNum);
                if (isNonTerminal(productionPointer)) {
                    //find out if the value is a nonterminal, assuming nonterminals are high valued
                    //TODO: need to add the whole when we see a nonterminal thing here
                    // this is where we generate more rows whenever we see a nonterminal
                    for (int i = 0; i < productions.size(); i++) {
                        LinkedList<Integer> production = productions.get(i);

                        if (production.get(0).equals(productionPointer)) {
                            //we use a static number because we know there is only 
                            //1 nonterminal followed by a dot in the production list
                            //eg all rules start with X 0, where X is a number

                            //we need to check to see if there are any other rules that match the rule we are going to copy over
                            if (checkForRules(production, listomania.get(tableNum)) == false) {
                                //we want to clone so we do not alter the productions list.
                                listomania.get(tableNum).add((LinkedList) production.clone());
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkForRules(List<Integer> a, LinkedList<List<Integer>> b) {
        // a is a list of terminals and non terminals
        // we want to see if that string of terminals and non terminals with the dot position is an exact match to any of the rules in b
        boolean output = false;
        for (int i = 0; i < b.size(); i++) {
            List<Integer> bItem = b.get(i);

            for (int j = 0; j < bItem.size(); j++) {
                if (a.get(j).intValue() != bItem.get(j).intValue()) {
                    //characters do not match 
                    output = false;
                    //characters do not need to match
                    break;
                } else {
                    // if the characters match, and continue to match, set it to true.
                    output = true;
                }
            }
            //
            if (output == true) {
                return true;
            }
        }

        return false;
    }

    private void addGoto(int tableNum, Integer leadingCharacter, int gotoTable) {
        System.out.println("goto:  " + tableNum + "\t" + leadingCharacter + "\t" + gotoTable);
        table.addGoto(tableNum, leadingCharacter, gotoTable);
    }

    private void addShift(int tableNum, Integer leadingCharacter, int gotoTable) {
        System.out.println("Shift:  " + tableNum + "\t" + leadingCharacter + "\t" + gotoTable);
        table.addShift(tableNum, leadingCharacter, gotoTable);
    }

    private void addReduce(int tableNum, Integer production, int count) {
        System.out.println("Reduce:  " + tableNum + "\t" + production);
        table.addReduce(tableNum, production, count);
    }
}
