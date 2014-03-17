package cs411.project.pkg2.parser;

/**
 * All the information the SLR parser should need to parse a token stream
 * @author Jovanni Cutigni
 */
public interface LRTable {
    public int getGoto(int tableNum, int nonterminal);
    public int getShift(int tableNum, int token);
    public int getReduce(int tableNum, int token);
    public int getReduceCount(int tableNum, int token);
}
