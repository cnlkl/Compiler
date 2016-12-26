package main;

import java.io.IOException;
import java.util.Stack;

import lexer.Lexer;
import parser.Chart;
import parser.Grammer;
import parser.Parser;


public class Main {

	public static void main(String[] args) throws IOException, CloneNotSupportedException {

//        String grammer = "S:E,E:CC,C:cC|d";
//        String grammer = "S:E,E:E+T|T,T:F|T*F,F:(E)|i" ;
//        String grammer = "S:Z,Z:XYZ|d,Y:c|#,X:Y|a";

        String grammer = "P:B," +
                "B:{DS}," +
                "D:DA|#," +
//                "D:AX," +
//                "X:AX|#," +
                "A:Ti;," +
                "T:T[m]|b," +
                "S:SC|#," +
//                "S:CY," +
//                "Y:CY|#" +
                "C:L=G;|f(G)C|f(G)CsC|w(G)C|dCw(G);|k;|B,"+
                "L:L[G]|i," +
                "G:GoJ|J," +
                "J:J&E|E," +
                "E:EeR|EnR|R," +
                "R:H<H|HlH|HgH|H>H|H," +
                "H:H+I|H-I|I," +
                "I:I*U|I/U|U," +
                "U:!U|-U|F," +
                "F:(G)|L|m|r|t|a";
        Chart chart = new Chart(grammer);
        chart.printAllStatus();
        Grammer grammer1 = Grammer.getInstance();
        grammer1.printAllProductionRules();
        chart.printParseChart();
        System.out.println();
        Parser parser = new Parser(new Lexer(), chart);
        parser.parse();
	}

}
