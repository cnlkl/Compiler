package lexer;


/*
 * Word 用于管理保留字、标识符
 */
public class Word extends Token {

	//词素
	private String lexeme;
	
	public static final Word
	AND = new Word("&&", Tag.AND),
	OR = new Word("||", Tag.OR),
	EQ = new Word("==", Tag.EQ),
	NE = new Word("!=", Tag.NE),
	LE = new Word("<=", Tag.LE),
	GE = new Word(">=", Tag.GE),
	MINUS = new Word("minus", Tag.MINUS),
	TRUE = new Word("true", Tag.TRUE),
	FALSE = new Word("false", Tag.FALSE),
	TEMP = new Word("temp",Tag.TEMP),
	IF = new Word("if", Tag.IF),
	ELSE = new Word("else", Tag.ELSE),
	WHILE = new Word("while", Tag.WHILE),
	DO = new Word("do", Tag.DO),
	BREAK = new Word("break", Tag.BREAK);
	
	/*
	 * 构造函数
	 * 
	 * @param lexeme 词素
	 * 
	 * @param tag 词法单元
	 */
	public Word(String lexeme, int tag) {
		super(tag);
		this.lexeme = lexeme;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	@Override
	public String toString() {
		return lexeme;
	}
	
	

}
