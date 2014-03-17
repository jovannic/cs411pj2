/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import cs411.project.lexer.Token;
import java.util.LinkedList;
import java.util.*;

/**
 *
 * @author Michael
 */
public class Table {
    public static final Integer DOT = -1; // the value of our dot
    private static final Integer AFTER_DOT = 2;
    private List<List<List<Integer>>> listomania;
    // this should not change, so it should be final
    private final List<List<Integer>> productions;
    // the grammar we're making a table for
    private final Grammar grammar;

    private HashSLRTable table;

    public static Table makeTable(Grammar grammar) {
        return new Table(grammar);
    }

    private Table(Grammar grammar) {
        this.grammar = grammar;
        this.productions = grammar.allRules();

        listomania = new LinkedList();
        table = new HashSLRTable();

        makeAi();
    }

    /**
     * This is a method for testing, so we can see the output of the tables.
     *
     * @return
     */
    public List<List<List<Integer>>> getLists() {
        return listomania;
    }
    public LRTable getTable() {
        return table;
    }

    private void makeAi() {
        //int tableNum = 0;
        int startProductionNum = 0;
        //this adds our first rule to the table
        List<Integer> rule = productions.get(startProductionNum);
        LinkedList<List<Integer>> ruleSq = new LinkedList();
        ruleSq.add(rule);
        listomania.add((LinkedList) ruleSq.clone());

        //if we have nonterminals after the dot, we want to add all of their rules to the table
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            addNonterminalsToTable(tableNum);
            generateTables(tableNum);
        }
    }

    private void generateTables(int tableNum) {
        // linked list index lookups aren't cheap, reuse
        List<List<Integer>> table = listomania.get(tableNum);

        //for each of the rules in X
        for (int ruleNum = 0; ruleNum < table.size(); ruleNum++) {
            LinkedList<List<Integer>> ruleList = new LinkedList<List<Integer>>(); //its called characterList but it is Integers...
            List<Integer> rule = table.get(ruleNum);

            int charNum = findAfterDot(rule);

            Integer leadingCharacter; //can be nonterminal or terminal, like cancer
            if (charNum < rule.size()) {
                leadingCharacter = rule.get(charNum);
            } else {
                leadingCharacter = Integer.MIN_VALUE;
            }

            if (leadingCharacter.intValue() == Integer.MIN_VALUE) {
                //get the first character
                Integer production = findProduction(new LinkedList(rule));
                int right = (rule.size() - 2);
                addReduce(tableNum, production, right);
                continue;
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
            for (List<Integer> ruleListItem : ruleList) {
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
    private int doesTableExist(List<List<Integer>> query) {
        //int tableNum = 0;
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            List<List<Integer>> table = listomania.get(tableNum);

            boolean flag = true;
            for (int i = 0; i < query.size() && i < table.size() ; i++) {
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
        return grammar.isTerminal(number);
    }

    private boolean isNonTerminal(int number) {
        return !isTerminal(number);
    }

    private void addNonterminalsToTable(int tableNum) {
        //int tableNum = 0;
        //int ruleNum = 0;
        List<List<Integer>> table = listomania.get(tableNum);
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
                        List<Integer> production = productions.get(i);

                        if (production.get(0).equals(productionPointer)) {
                            //we use a static number because we know there is only 
                            //1 nonterminal followed by a dot in the production list
                            //eg all rules start with X 0, where X is a number

                            //we need to check to see if there are any other rules that match the rule we are going to copy over
                            if (checkForRules(production, listomania.get(tableNum)) == false) {
                                //we want to clone so we do not alter the productions list.
                                table.add(new LinkedList<Integer>(production));
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean checkForRules(List<Integer> a, List<List<Integer>> b) {
        // a is a list of terminals and non terminals
        // we want to see if that string of terminals and non terminals with the dot position is an exact match to any of the rules in b
        boolean output = false;
        
        for (int i = 0; i < b.size(); i++) {
            List<Integer> bItem = b.get(i);
            if (a.size() == b.get(i).size()) {
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
            }
            //
            if (output == true) {
                return true;
            }
        }

        return false;
    }

    private boolean addGoto(int tableNum, Integer leadingCharacter, int gotoTable) {
        System.out.println("\tGOTO:  \t" + tableNum + "\t" + grammar.nameOrIdOf(leadingCharacter) + ": " + gotoTable);
        return table.addGoto(tableNum, leadingCharacter, gotoTable);
    }

    private boolean addShift(int tableNum, Integer leadingCharacter, int gotoTable) {
        System.out.println("\tSHIFT: \t" + tableNum + "\t" + grammar.nameOrIdOf(leadingCharacter) + ": " + gotoTable);
        return table.addShift(tableNum, leadingCharacter, gotoTable);
    }

    private boolean addReduce(int tableNum, Integer production, int count) {
        System.out.println("\tREDUCE:\t" + tableNum + "\tr" + production + "\t-" + count);
        return table.addReduce(tableNum, production, count);
    }

    private Integer findProduction(LinkedList<Integer> prod) {
        prod.remove(prod.size() - 1);
        prod.add(1, DOT);
        
        for(int i = 0; i < productions.size(); i++) {
            if(productions.get(i).size() == prod.size()) {
                for(int charNum = 0; charNum < prod.size(); charNum++) {
                    if(productions.get(i).get(charNum).intValue() != prod.get(charNum).intValue()) {
                        break;
                    } else if (charNum == prod.size() - 1) {
                        //if all of the characters match up to this point, and we are at the last character
                        // and those characters match
                        // then this is the right production
                        return i;
                    }
                }
            }
        }
        System.out.println("Can't Find production");
        return null; // should never return null
    }
}
