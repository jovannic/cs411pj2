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
        tableTest();
        emptyProductionTest();
        realTest();
    }
    
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
    
    public void printList(List<List<List<Integer>>> l) {
        System.out.println();
        for(int i = 0; i < l.size(); i++) {
            
            System.out.println("Table number: " + i);
            for (int j = 0; j < l.get(i).size(); j++) {
                
                for(int k = 0; k < l.get(i).get(j).size(); k++) {
                    System.out.print((/*g.nameOf(l.get(i).get(j).get(k)) != null ? g.nameOf(l.get(i).get(j).get(k)) :*/ (l.get(i).get(j).get(k))) + " ");
                    
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
