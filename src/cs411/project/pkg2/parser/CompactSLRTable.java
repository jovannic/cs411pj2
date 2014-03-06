package cs411.project.pkg2.parser;

import java.util.Arrays;

/**
 * Created by Jovanni on 3/4/14.
 */
public class CompactSLRTable {
    private SwitchTable shifts;
    private int[] reduce;
    private SwitchTable gotos;

    private CompactSLRTable(int numTables) {
        shifts = new SwitchTable(numTables);
        reduce = new int[numTables];
        gotos = new SwitchTable(numTables);
    }

    public boolean addShift(int tableID, int symbol, int next) {
        return shifts.setNext(tableID, symbol, next);
    }

    public boolean addGoto(int tableID, int symbol, int next) {
        return gotos.setNext(tableID, symbol, next);
    }

    public  boolean addReduce(int tableID, int symbol, int next) {
        if (reduce[tableID] == 0) {
            reduce[tableID] = next;
        } else {
            // conflict
        }
        return false;
    }

    // Mac's favorite: inner classes
    private class SwitchTable {
        private static final int DELIMITER = -1;
        private int[] start; // tableID -> index
        private int size = 0;
        private int[] symbol; // index -> symbol
        private int[] next; // index -> tableID

        private SwitchTable(int numTables) {
            start = new int[numTables];
            Arrays.fill(start, -1);
            symbol = new int[numTables];
            next = new int[numTables];
        }

        private int getNext(int tableID, int symbol) {
            int index = start[tableID];

            if (index != -1) {
                int n = this.symbol.length;
                for (int i = index; i < n; i++) {
                    int s = this.symbol[i];
                    if (s == DELIMITER)
                        break;
                    else if (s == symbol)
                        return next[i];
                }
            }
            return -1;
        }

        private boolean setNext(int tableID, int symbol, int next) {
            int index = start[tableID];

            if (index != -1) {
                int n = this.symbol.length;
                for (int i = index; i < n; i++) {
                    int s = this.symbol[i];
                    if (s == DELIMITER) {
                        return false;
                    } else if (s == 0) {
                        // TODO
                        return true;
                    }
                }
            }
            return false;
        }

        private void ensureCapacity(int minSize) {
            int oldSize = symbol.length;
            if (oldSize > minSize)
                return;

            int n = oldSize * 2;
            while (n < minSize)
                n *= 2;

            symbol = Arrays.copyOf(symbol, n);
            next = Arrays.copyOf(next, n);
        }
    }
}
