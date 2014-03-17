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
    public static void runAllTests() {
        tableTest();
        emptyProductionTest();
        realTest();
    }

    private static void tableTest() {
        List<List<Integer>> l = new LinkedList();
        l.add(Grammar.makeRule(10, 5, 20));
        l.add(Grammar.makeRule(20, 6));
        l.add(Grammar.makeRule(20, 7));

        Grammar g = new Grammar(l, 9);
        Table t = new Table(g);
        t.makeAi();
        printList(t.getLists(), g);
    }
    
    private static void emptyProductionTest() {
        List<List<Integer>> l = new LinkedList();
        l.add(Grammar.makeRule(10, 5, 2, 30, 7));
        l.add(Grammar.makeEmpty(20));
        l.add(Grammar.makeEmpty(30));

        Grammar g = new Grammar(l, 9);
        Table t = new Table(g);
        t.makeAi();
        printList(t.getLists(), g);
    }
    
    private static void realTest() {
        List<List<Integer>> l = new LinkedList();
        l.add(Grammar.makeRule(47, 48));
        l.add(Grammar.makeRule(48, 49));
        l.add(Grammar.makeRule(48, 49, 48));

        Grammar g = new Grammar(l,46);
        Table t = new Table(g);
        t.makeAi();
        printList(t.getLists(), g);
    }

    public static void printList(List<List<List<Integer>>> l, Grammar g) {
        System.out.println();
        for(int i = 0; i < l.size(); i++) {
            List<List<Integer>> table = l.get(i);
            System.out.println("Table number: " + i);

            for (int j = 0; j < l.get(i).size(); j++) {
                List<Integer> tableItem = table.get(j);

                for (int k = 0; k < l.get(i).get(j).size(); k++) {
                    int item = tableItem.get(k);
                    String name = (item == Table.DOT) ? "." : g.nameOf(item);
                    if (name == null)
                        name = Integer.toString(item);

                    System.out.print(name + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    public static void printGrammar(Grammar g) {
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
