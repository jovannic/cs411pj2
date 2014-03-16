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
    }
    
    private static void tableTest() {
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
        Table t = new Table(l,9);
        t.makeAi();
        printList(t.getLists());
    }
    
    private static void emptyProductionTest() {
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
    
    private static void printList(List<List<List<Integer>>> l) {
        System.out.println();
        for(int i = 0; i < l.size(); i++) {
            
            System.out.println("Table number: " + i);
            for (int j = 0; j < l.get(i).size(); j++) {
                
                for(int k = 0; k < l.get(i).get(j).size(); k++) {
                    System.out.print(l.get(i).get(j).get(k) + " ");
                    
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
