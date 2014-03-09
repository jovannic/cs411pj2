/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cs411.project.pkg2.parser;

import java.io.File;
import java.io.IOException;

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

        //is this even working
    }
}
