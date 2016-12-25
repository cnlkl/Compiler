package lexer;


/*
 * 浮点数
 */
public class Real extends Token {
	
	private final double value;
	
	public Real(double value) {
		super(Tag.REAL);
		this.value = value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}
}
