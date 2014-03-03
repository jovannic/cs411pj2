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
    private LinkedList<LinkedList<LinkedList<Integer>>> listomania;
    private LinkedList<LinkedList<Integer>> productions;

    public Table(List<List<Integer>> productions) {
        this.productions = productions;
        listomania = new LinkedList();
    }

    public void makeAi() {
        int tableNum = 0;
        int ruleNum = 0;
        int charNum = 0;
        int i = 0;
        int j = 0;
        LinkedList<Integer> rule = productions.get(tableNum);
        LinkedList<LinkedList<Integer>> ruleSq = new LinkedList();
        ruleSq.add(rule);
        listomania.add(ruleSq);
        while (listomania.get(tableNum).get(ruleNum).get(charNum) != DOT) {
            // we want to find the value of J where we see 0, or rather our dot
            charNum++;
        }
        int dotIndex = charNum++; // add 1 again to see the information after the dot
        // also save the position of the dot
        if (listomania.get(tableNum).get(ruleNum).get(charNum) > nonterminal) {
            //find out if the value is a nonterminal, assuming nonterminals are high valued
            //TODO: need to add the whole when we see a nonterminal thing here
            
        } else {
            //shifting done
            LinkedList<Integer> clone = (LinkedList<Integer>) listomania.get(tableNum).get(ruleNum).clone();
            //make a new table for the shift
            listomania.add(tableNum + 1, new LinkedList());
            // add the new rule to the table
            listomania.get(tableNum + 1).add(clone);
            //swap the dot to allow for shift
            listomania.get(tableNum).get(ruleNum).remove(dotIndex);
            listomania.get(tableNum).get(ruleNum).add(charNum, DOT);
        }

    }
}
