package cs411.project.pkg2.parser;

/**
 * Created by Jovanni on 3/5/14.
 */
public interface SLRTable {
    public boolean addGoto(int tableNum, int symbol, int gotoTable);
    public boolean addShift(int tableNum, int symbol, int gotoTable);
    public boolean addReduce(int tableNum, int symbol);

    public int getGoto(int tableNum, int symbol);
    public int getShift(int tableNum, int symbol);
    public int getReduce(int tableNum);
}
