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
    private LinkedList<LinkedList<LinkedList<Integer>>> listomania;
    //this should not change, so it should be final
    private final LinkedList<LinkedList<Integer>> productions;

    public Table(LinkedList<LinkedList<Integer>> productions) {
        this.productions = productions;
        listomania = new LinkedList();
    }

    public void makeAi() {
        //int tableNum = 0;
        int startProductionNum = 0;
        //this adds our first rule to the table
        LinkedList<Integer> rule = productions.get(startProductionNum);
        LinkedList<LinkedList<Integer>> ruleSq = new LinkedList();
        ruleSq.add(rule);
        listomania.add(ruleSq);

        //if we have nonterminals after the dot, we want to add all of their rules to the table
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            addNonterminalsToTable(tableNum);
            generateTables(tableNum);
        }

        //bench this code
        //shifting done
//        LinkedList<Integer> clone = (LinkedList<Integer>) listomania.get(tableNum).get(ruleNum).clone();
//        //make a new table for the shift
//        listomania.add(tableNum + 1, new LinkedList());
//        // add the new rule to the table
//        listomania.get(tableNum + 1).add(clone);
//        //swap the dot to allow for shift
//        listomania.get(tableNum).get(ruleNum).remove(dotIndex);
//        listomania.get(tableNum).get(ruleNum).add(charNum, DOT);


    }

    private void generateTables(int tableNum) {
        //int tableNum = 0;
        //int ruleNum = 0;
        int charNum = 0;
        Integer leadingCharacter; //can be nonterminal or terminal, like cancer
        LinkedList<LinkedList<Integer>> ruleList = new LinkedList(); //its called characterList but it is Integers...
        //for each of the rules in X
        for (int ruleNum = 0; ruleNum < listomania.get(tableNum).size(); ruleNum++) {
            charNum = findAfterDot(tableNum, ruleNum);
            if(charNum < listomania.get(tableNum).get(ruleNum).size()) {
            leadingCharacter = listomania.get(tableNum).get(ruleNum).get(charNum);
            } else {
                leadingCharacter = -1;
            }
            if(leadingCharacter == -1) {
                //get the first character
                Integer production = listomania.get(tableNum).get(ruleNum).get(0);
                addReduce(tableNum, production);
            }
            //add all of the rules that match this character
            for (int i = 0; i < listomania.get(tableNum).size(); i++) {
                charNum = findAfterDot(tableNum, i);
                //if it is the same as our leading character
                if (listomania.get(tableNum).get(i).get(charNum) == leadingCharacter) {
                    //add it to the list
                    ruleList.addAll((LinkedList) listomania.get(tableNum).get(i).clone());
                    //we need to clone because we want to be able to manipulate these rules.
                }
                //we need to shift all of the rules
                // that means move the zero over one spot

            }
            for (int i = 0; i < ruleList.size(); i++) {
                int afterDotIndex = 0;

                while (ruleList.get(i).get(afterDotIndex) != DOT) {
                    // we want to find the value of J where we see 0, or rather our dot
                    afterDotIndex++;
                }
                int dotIndex = afterDotIndex++;
                //make sure we are not out of bounds
                if (afterDotIndex < ruleList.get(i).size()) {
                    //remove dot
                    listomania.get(tableNum).get(ruleNum).remove(dotIndex);
                    //add the dot to its pervious position +1
                    listomania.get(tableNum).get(ruleNum).add(afterDotIndex, DOT);
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
                listomania.addAll((LinkedList) ruleList);
                gotoTable = listomania.size() - 1;

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
        boolean flag = true;
        Integer a, b;
        for (int tableNum = 0; tableNum < listomania.size(); tableNum++) {
            flag = true;
            for (int i = 0; i < query.size(); i++) {
                for (int j = 0; j < query.get(i).size(); j++) {
                    a = listomania.get(tableNum).get(i).get(j);
                    b = query.get(i).get(j);
                    if (a != b) {
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

    private int findAfterDot(int tableNum, int ruleNum) {
        int charNum = 0;
        while (listomania.get(tableNum).get(ruleNum).get(charNum) != DOT) {
            // we want to find the value of J where we see 0, or rather our dot
            charNum++;
        }
        int dotIndex = charNum++; // add 1 again to see the information after the dot
        // also save the position of the dot
        return charNum;
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
        int charNum = 0;
        for (int ruleNum = 0; ruleNum < listomania.get(tableNum).size(); ruleNum++) {
            //find the point after the dot dot
            findAfterDot(tableNum, ruleNum);
            //get the character after the dot
            int productionPointer = listomania.get(tableNum).get(ruleNum).get(charNum);
            if (isNonTerminal(productionPointer)) {
                //find out if the value is a nonterminal, assuming nonterminals are high valued
                //TODO: need to add the whole when we see a nonterminal thing here
                // this is where we generate more rows whenever we see a nonterminal
                for (int i = 0; i < productions.get(tableNum).size(); i++) {
                    if (productions.get(i).get(AFTER_DOT) == productionPointer) {
                        //we use a static number because we know there is only 
                        //1 nonterminal followed by a dot in the production list
                        //eg all rules start with X 0, where X is a number

                        //we need to check to see if there are any other rules that match the rule we are going to copy over
                        if (checkForRules(productions.get(i), listomania.get(tableNum)) == true) {
                            //we want to clone so we do not alter the productions list.
                            listomania.get(tableNum).addAll((LinkedList) productions.get(i).clone());
                        }
                    }
                }

            }
        }
    }

    private boolean checkForRules(LinkedList<Integer> a, LinkedList<LinkedList<Integer>> b) {
        // a is a list of terminals and non terminals
        // we want to see if that string of terminals and non terminals with the dot position is an exact match to any of the rules in b
        boolean output = false;
        for (int i = 0; i < b.size(); i++) {
            for (int j = 0; i < b.get(i).size(); j++) {
                if (a.get(j) != b.get(i).get(j)) {
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addShift(int tableNum, Integer leadingCharacter, int gotoTable) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private void addReduce(int tableNum, Integer production) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
