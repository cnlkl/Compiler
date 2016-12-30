package inter;

import lexer.Lexer;

public class Node {
	
	private int mLexLine = 0;
	
	public static int labels = 0;
	
	public Node() {
		mLexLine = Lexer.line;
	}
	
	public void error(String s){
		throw new Error("near line" + mLexLine + ": " + s);
	}
	
	public int newLabel() {
		return ++labels;
	}
	
	public void emitLabel(int i) {
		System.out.print("L" + i + ":");
	}
	
	public void emit(String s) {
		System.out.println("\t" + s);
	}
}
	