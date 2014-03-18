/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import cs411.project.lexer.BasicFileStream;
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
        Grammar g = Grammar.load(new File("grammar.txt"));

        Tests.printGrammar(g);

        Table macTable = Table.makeTable(g);

        Tests.printList(macTable.getLists(), g);

        LRTable lrTable = macTable.getTable();
        Parser parser = new Parser(lrTable, g);

        // Jovanni's lexer
        Lexer lexer = new Lexer();

        parseFile(lexer, parser, "FailTest1.txt");
    }

    public static void parseFile(Lexer lexer, Parser parser, String filename) throws IOException {
        // token stream using Jovanni's lexer to lex a file
        LexingStream stream = new JovanniLexingStream(lexer, new BasicFileStream(filename));

        System.out.println("Parsing \"" + filename + "\":\n");
        // parse
        List<Integer> output = parser.parse(stream);
    }
}
