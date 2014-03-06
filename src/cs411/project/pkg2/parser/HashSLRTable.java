package cs411.project.pkg2.parser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jovanni on 3/4/14.
 */
public class HashSLRTable implements SLRTable {
    private Map<Integer, Integer> reduce = new HashMap<Integer, Integer>();
    private Map<Integer, Map<Integer, Integer>> shift = new HashMap<Integer, Map<Integer, Integer>>();
    private Map<Integer, Map<Integer, Integer>> gotos = new HashMap<Integer, Map<Integer, Integer>>();

    @Override
    public boolean addGoto(int tableNum, int symbol, int gotoTable) {
        add(gotos, tableNum, symbol, gotoTable);
        return true;
    }

    @Override
    public boolean addShift(int tableNum, int symbol, int gotoTable) {
        add(shift, tableNum, symbol, gotoTable);
        return true;
    }

    @Override
    public boolean addReduce(int tableNum, int symbol) {
        reduce.put(tableNum, symbol);
        return true;
    }

    @Override
    public int getGoto(int tableNum, int symbol) {
        return get(gotos, tableNum, symbol);
    }

    @Override
    public int getShift(int tableNum, int symbol) {
        return get(gotos, tableNum, symbol);
    }

    @Override
    public int getReduce(int tableNum) {
        Integer r = reduce.get(tableNum);
        return r != null ? r : -1;
    }

    private int get(Map<Integer, Map<Integer, Integer>> map, int tableID, int symbol) {
        Map<Integer, Integer> table = map.get(tableID);
        return table != null ? table.get(symbol) : -1;
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
