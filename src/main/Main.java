package main;

import java.io.IOException;
import parser.Chart;
import parser.Grammer;


public class Main {

	public static void main(String[] args) throws IOException, CloneNotSupportedException {

//        String grammer = "S:E,E:CC,C:cC|d";
        String grammer = "S:E," +

                         "E:E+T" +
                          "|T," +

                         "T:F" +
                          "|T*F," +

                         "F:(E)" +
                          "|i";
//        String grammer = "S:Z,Z:XYZ|d,Y:c|#,X:Y|a";

        Chart chart = new Chart(grammer);
        chart.printAllStatus();
        Grammer grammer1 = Grammer.getInstance();
        grammer1.printAllProductionRules();
        chart.printParseChart();

	}

}
