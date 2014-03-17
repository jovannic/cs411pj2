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
        // run diagnostic tests
        Tests.runAllTests();


        Grammar g = Grammar.load(new File("grammar.txt"));

        Tests.printGrammar(g);

        Table macTable = Table.makeTable(g);

        Tests.printList(macTable.getLists(), g);

        SLRTable lrTable = macTable.getTable();
        Parser parser = new Parser(lrTable, g, 0); // TODO: figure out where to accept correctly

        // Jovanni's lexer
        Lexer lexer = new Lexer();

        // token stream using Jovanni's lexer to lex a file
        LexingStream stream = new JovanniLexingStream(lexer, new BasicFileStream("input2.txt"));

        // parse
        List<Integer> output = parser.parse(stream);
    }
}
