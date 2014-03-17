package cs411.project.pkg2.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap based reference implementation of the LRTable interface.
 * <p>inefficient, not compact, but easy to implement, hard to screw up.</p>
 * @author Jovanni Cutigni
 */
public class HashSLRTable implements LRTable {
    private Map<Integer, Integer> reduce = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> reduceCount = new HashMap<Integer, Integer>();
    private Map<Integer, Map<Integer, Integer>> shift = new HashMap<Integer, Map<Integer, Integer>>();
    private Map<Integer, Map<Integer, Integer>> gotos = new HashMap<Integer, Map<Integer, Integer>>();

    public boolean addGoto(int tableNum, int nonterminal, int gotoTable) {
        add(gotos, tableNum, nonterminal, gotoTable);
        return true;
    }

    public boolean addShift(int tableNum, int token, int gotoTable) {
        int s;
        if (getReduce(tableNum, token) != -1) {
            System.out.println("S/R @ " + tableNum);
        } else if ((s = getShift(tableNum, token)) != -1) {
            System.out.println("S/S @ " + tableNum
                    + " where " + s + (s == gotoTable ? " (same)" : ""));
            return false;
        }

        add(shift, tableNum, token, gotoTable);
        return true;
    }

    public boolean addReduce(int tableNum, int ruleNum, int count) {
        int r;
        if ((r = getReduce(tableNum, -1)) != -1) {
            System.out.println("R/R @ " + tableNum
                    + " where " + r + (r == ruleNum ? " (same)" : ""));
            return false;
        } else if (shift.containsKey(tableNum)) {
            System.out.println("S/R @ " + tableNum);
        }

        reduce.put(tableNum, ruleNum);
        reduceCount.put(tableNum, count);
        return true;
    }

    @Override
    public int getGoto(int tableNum, int nonterminal) {
        return get(gotos, tableNum, nonterminal);
    }

    @Override
    public int getShift(int tableNum, int token) {
        return get(shift, tableNum, token);
    }

    @Override
    public int getReduce(int tableNum, int token) {
        Integer r = reduce.get(tableNum);
        return r != null ? r : -1;
    }

    @Override
    public int getReduceCount(int tableNum, int token) {
        return reduceCount.get(tableNum);
    }

    private int get(Map<Integer, Map<Integer, Integer>> map, int tableID, int symbol) {
        Map<Integer, Integer> table = map.get(tableID);
        if (table != null) {
            Integer value = table.get(symbol);
            if (value != null) {
                return value;
            }
        }
        return -1;
    }

    private void add(Map<Integer, Map<Integer, Integer>> map, int tableID, int symbol, int gotoTable) {
        Map<Integer, Integer> table = map.get(tableID);
        if (table == null) {
            table = new HashMap<Integer, Integer>();
            map.put(tableID, table);
        }

        table.put(symbol, gotoTable);
    }
}
