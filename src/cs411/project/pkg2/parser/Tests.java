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
        Table t = Table.makeTable(g);
        printList(t.getLists(), g);
    }
    
    private static void emptyProductionTest() {
        List<List<Integer>> l = new LinkedList();
        l.add(Grammar.makeRule(10, 5, 2, 30, 7));
        l.add(Grammar.makeEmpty(20));
        l.add(Grammar.makeEmpty(30));

        Grammar g = new Grammar(l, 9);
        Table t = Table.makeTable(g);
        printList(t.getLists(), g);
    }
    
    private static void realTest() {
        List<List<Integer>> l = new LinkedList();
        l.add(Grammar.makeRule(47, 48));
        l.add(Grammar.makeRule(48, 49));
        l.add(Grammar.makeRule(48, 49, 48));

        Grammar g = new Grammar(l,46);
        Table t = Table.makeTable(g);
        printList(t.getLists(), g);
    }

    public static void printList(List<List<List<Integer>>> l, Grammar g) {
        System.out.println();
        for(int i = 0; i < l.size(); i++) {
            List<List<Integer>> table = l.get(i);
            System.out.println("Table " + i + ":");

            for (int j = 0; j < table.size(); j++) {
                List<Integer> tableItem = table.get(j);

                System.out.print("\t" + usefulName(tableItem.get(0), g) + " = ");
                for (int k = 1; k < tableItem.size(); k++) {
                    String name = usefulName(tableItem.get(k), g);

                    System.out.print(name + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
    
    private static String usefulName(int id, Grammar g) {
        String name = (id == Table.DOT) ? "." : g.nameOf(id);
        if (name == null)
            name = Integer.toString(id);
        return name;
    }

    public static void printGrammar(Grammar g) {
        List<List<Integer>> allRules = g.allRules();

        int num = 0;
        for (List<Integer> rule : allRules) {
            System.out.print(num++ + ". " + g.nameOf(rule.get(0)) + " ::= ");

            List<Integer> slice = rule.subList(2, rule.size());
            for (int id : slice) {
                System.out.print(g.nameOf(id) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
