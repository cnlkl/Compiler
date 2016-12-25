package lexer;

/*
 * 词法单元
 */

public class Token {
	private final int tag;
	
	public Token(int tag) {
		this.tag = tag;
	}
	
	public int getTag() {
		return tag;
	}
	
	@Override
	public String toString() {
		return String.valueOf((char)tag);
	}
}
