package cs411.project.pkg2.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * HashMap based reference implementation of the SLRTable interface.
 * <p>inefficient, not compact, but easy to implement, hard to screw up.</p>
 * @author Jovanni Cutigni
 */
public class HashSLRTable implements SLRTable {
    private Map<Integer, Integer> reduce = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> reduceCount = new HashMap<Integer, Integer>();
    private Map<Integer, Map<Integer, Integer>> shift = new HashMap<Integer, Map<Integer, Integer>>();
    private Map<Integer, Map<Integer, Integer>> gotos = new HashMap<Integer, Map<Integer, Integer>>();

    public boolean addGoto(int tableNum, int symbol, int gotoTable) {
        add(gotos, tableNum, symbol, gotoTable);
        return true;
    }

    public boolean addShift(int tableNum, int symbol, int gotoTable) {
        int s;
        if (getReduce(tableNum) != -1) {
            System.out.println("S/R @ " + tableNum);
        } else if ((s = getShift(tableNum, symbol)) != -1) {
            System.out.println("S/S @ " + tableNum
                    + " where " + s + (s == gotoTable ? " (same)" : ""));
            return false;
        }

        add(shift, tableNum, symbol, gotoTable);
        return true;
    }

    public boolean addReduce(int tableNum, int symbol, int count) {
        int r;
        if ((r = getReduce(tableNum)) != -1) {
            System.out.println("R/R @ " + tableNum
                    + " where " + r + (r == symbol ? " (same)" : ""));
            return false;
        } else if (shift.containsKey(tableNum)) {
            System.out.println("S/R @ " + tableNum);
        }

        reduce.put(tableNum, symbol);
        reduceCount.put(tableNum, count);
        return true;
    }

    @Override
    public int getGoto(int tableNum, int symbol) {
        return get(gotos, tableNum, symbol);
    }

    @Override
    public int getShift(int tableNum, int symbol) {
        return get(shift, tableNum, symbol);
    }

    @Override
    public int getReduce(int tableNum) {
        Integer r = reduce.get(tableNum);
        return r != null ? r : -1;
    }

    @Override
    public int getReduceCount(int tableNum) {
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
