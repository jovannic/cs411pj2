package cs411.project.pkg2.parser;

/**
 * All the information the SLR parser should need to parse a token stream
 * @author Jovanni Cutigni
 */
public interface SLRTable {
    public boolean addGoto(int tableNum, int symbol, int gotoTable);
    public boolean addShift(int tableNum, int symbol, int gotoTable);
    public boolean addReduce(int tableNum, int symbol, int count);

    public int getGoto(int tableNum, int symbol);
    public int getShift(int tableNum, int symbol);
    public int getReduce(int tableNum);
    public int getReduceCount(int tableNum);
}
