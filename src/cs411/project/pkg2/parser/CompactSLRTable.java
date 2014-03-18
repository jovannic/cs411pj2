package cs411.project.pkg2.parser;

import java.util.Arrays;

/**
 * The compact switch + linear search table structure described in class
 * @author Jovanni Cutigni
 */
public class CompactSLRTable implements LRTable {
    private SwitchTable shifts;
    private int[] reduce;
    private int[] reduceCount;
    private SwitchTable gotos;

    public CompactSLRTable() {
        shifts = new SwitchTable();
        reduce = new int[128];
        Arrays.fill(reduce, -1);
        reduceCount = new int[128];
        gotos = new SwitchTable();
    }

    public boolean addGoto(int tableNum, int nonterminal, int gotoTable) {
        gotos.add(tableNum, nonterminal, gotoTable);
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

        shifts.add(tableNum, token, gotoTable);
        return true;
    }

    public boolean addReduce(int tableNum, int ruleNum, int count) {
        int r;
        if ((r = getReduce(tableNum, -1)) != -1) {
            System.out.println("R/R @ " + tableNum
                    + " where " + r + (r == ruleNum ? " (same)" : ""));
            return false;
        } else if (shifts.hasTable(tableNum)) {
            System.out.println("S/R @ " + tableNum);
        }

        ensureReduceCapacity(tableNum + 1);
        reduce[tableNum] = ruleNum;
        reduceCount[tableNum] = count;
        return true;
    }

    @Override
    public int getGoto(int tableNum, int nonterminal) {
        return gotos.get(tableNum, nonterminal);
    }

    @Override
    public int getShift(int tableNum, int token) {
        return shifts.get(tableNum, token);
    }

    @Override
    public int getReduce(int tableNum, int token) {
        return tableNum < reduce.length ? reduce[tableNum] : -1;
    }

    @Override
    public int getReduceCount(int tableNum, int token) {
        return tableNum < reduce.length ? reduceCount[tableNum] : -1;
    }

    private void ensureReduceCapacity(int minSize) {
        int oldSize = reduce.length;
        if (oldSize < minSize) {
            int n = nextSize(oldSize, minSize);
            reduce = Arrays.copyOf(reduce, n);
            reduceCount = Arrays.copyOf(reduceCount, n);
            // fill rest with -1
            for (int i = oldSize; i < n; i++) {
                reduce[i] = -1;
            }
        }
    }

    private static int nextSize(int oldSize, int minSize) {
        return nextPower2(Math.max(oldSize, minSize));
    }

    private static int nextPower2(int n) {
        // round n up to the next power of 2
        // source: Hacker's Delight 2nd ed. by H. Warren
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        return n + 1;
    }

    // Mac's favorite: inner classes
    private class SwitchTable {
        private static final int DELIMITER = -1;

        private int[] start; // tableID -> index

        private int size = 0;
        private int[] symbols; // index -> symbol
        private int[] values; // index -> tableNum or ruleNum

        private SwitchTable() {
            start = new int[128];
            Arrays.fill(start, -1);

            symbols = new int[128];
            values = new int[128];
        }

        private int get(int tableID, int symbol) {
            if (tableID < start.length) {
                int index = start[tableID];

                if (index != -1) {
                    int n = symbols.length;
                    for (int i = index; i < n; i++) {
                        int s = this.symbols[i];
                        if (s == DELIMITER)
                            break;
                        else if (s == symbol)
                            return values[i];
                    }
                }
            }
            return -1;
        }

        private boolean add(int tableID, int symbol, int value) {
            ensureStartCapacity(tableID + 1);
            int index = start[tableID];

            if (index != -1) {
                // table exists, add to table, if current last table
                int n = size;
                for (int i = index; i < n; i++) {
                    int s = symbols[i];

                    if (s == DELIMITER && i == size - 1) {
                        ensureSymbolCapacity(size + 1);

                        symbols[i] = symbol; // overwrite DELIMITER
                        symbols[i + 1] = DELIMITER; // moved DELIMITER
                        values[i] = value;

                        size += 1;

                        return true;
                    }
                }
            } else {
                // table doesn't exist yet: add at end
                ensureSymbolCapacity(size + 2);

                index = size;
                start[tableID] = index;

                symbols[index] = symbol;
                symbols[index + 1] = DELIMITER;
                values[index] = value;

                size += 2;

                return true;
            }
            return false;
        }

        private boolean hasTable(int tableID) {
            return tableID < start.length && start[tableID] != -1;
        }

        private void ensureStartCapacity(int minSize) {
            int oldSize = start.length;
            if (oldSize < minSize) {
                int n = nextSize(oldSize, minSize);
                start = Arrays.copyOf(start, n);
                // fill rest with -1
                for (int i = oldSize; i < n; i++) {
                    start[i] = -1;
                }
            }
        }

        private void ensureSymbolCapacity(int minSize) {
            int oldSize = symbols.length;
            if (oldSize < minSize) {
                int n = nextSize(oldSize, minSize);
                symbols = Arrays.copyOf(symbols, n);
                values = Arrays.copyOf(values, n);
            }
        }
    }
}
