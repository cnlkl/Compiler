package lexer;

/*
 * 整形常量
 */

public class Num extends Token {
	
	private final int value;
	
	public Num(int value) {
		super(Tag.NUM);
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
