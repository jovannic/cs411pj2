/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Michael
 */
public class Tests {
    Grammar g;

    Tests(Grammar g) {
        this.g = g;
    }

    public void runAllTests() {
        printGrammar();
//        tableTest();
//        emptyProductionTest();
//        realTest();
    }
    
/*
    private void tableTest() {
        List<List<Integer>> l = new LinkedList();
        List<Integer> li = new LinkedList();
        li.add(new Integer(10));
        li.add(new Integer(0));
        li.add(new Integer(5));
        li.add(new Integer(20));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(20));
        li.add(new Integer(0));
        li.add(new Integer(6));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(20));
        li.add(new Integer(0));
        li.add(new Integer(7));
        l.add(li);
        Table t = new Table(l,9);
        t.makeAi();
        printList(t.getLists());
    }
    
    private void emptyProductionTest() {
        List<List<Integer>> l = new LinkedList();
        List<Integer> li = new LinkedList();
        li.add(new Integer(10));
        li.add(new Integer(0));
        li.add(new Integer(5));
        li.add(new Integer(20));
        li.add(new Integer(30));
        li.add(new Integer(7));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(20));
        li.add(new Integer(0));
        li.add(new Integer(-1));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(30));
        li.add(new Integer(0));
        l.add(li);
        li = new LinkedList();
        Table t = new Table(l,9);
        t.makeAi();
        printList(t.getLists());
    }
    
    private void realTest() {
        List<List<Integer>> l = new LinkedList();
        List<Integer> li = new LinkedList();
        li.add(new Integer(47));
        li.add(new Integer(0));
        li.add(new Integer(48));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(48));
        li.add(new Integer(0));
        li.add(new Integer(49));
        l.add(li);
        li = new LinkedList();
        li.add(new Integer(48));
        li.add(new Integer(0));
        li.add(new Integer(49));
        li.add(new Integer(48));
        l.add(li);
        li = new LinkedList();
        Table t = new Table(l,46);
        t.makeAi();
        printList(t.getLists());
    }
*/

    public void printList(List<List<List<Integer>>> l) {
        System.out.println();
        for(int i = 0; i < l.size(); i++) {
            List<List<Integer>> table = l.get(i);
            System.out.println("Table number: " + i);

            for (int j = 0; j < l.get(i).size(); j++) {
                List<Integer> tableItem = table.get(j);

                for (int k = 0; k < l.get(i).get(j).size(); k++) {
                    String name = (k == Table.DOT) ? "." : g.nameOf(tableItem.get(k));
                    System.out.print(name + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public void printGrammar() {
        List<List<Integer>> allRules = g.allRules();

        for (List<Integer> rule : allRules) {
            System.out.print(g.nameOf(rule.get(0)) + " ::= ");

            List<Integer> slice = rule.subList(2, rule.size());
            for (int id : slice) {
                System.out.print(g.nameOf(id) + " ");
            }
            System.out.println();
        }
    }
}
