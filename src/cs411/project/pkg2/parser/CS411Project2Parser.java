/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import cs411.project.lexer.BasicFileStream;
import cs411.project.lexer.CharStream;
import cs411.project.lexer.Lexer;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * CS 411 Project 2 Parser
 * @author Michael
 * @author Jovanni Cutigni
 */
public class CS411Project2Parser {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // TODO code application logic here

        Tests.runAllTests();

        Grammar g = Grammar.load(new File("grammar.txt"));

        Tests.printGrammar(g);

        Table macTable = new Table(g);
        macTable.makeAi();

        Tests.printList(macTable.getLists(), g);

        SLRTable table = macTable.getTable();
        Parser parser = new Parser(table, 0, g.allRules()); // TODO: figure out where to accept correctly

        // Jovanni's lexer
        Lexer lexer = new Lexer();
        CharStream stream = new BasicFileStream("input2.txt");

        // TODO: do above TODOs so this acctually works
        try {
            List<Integer> output = parser.parse(new JovanniLexingStream(lexer, stream));
        } catch(IllegalArgumentException e) {
            System.out.println("Useful Information about the error that just happened");
        }
    }
}
