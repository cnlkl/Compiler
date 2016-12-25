package lexer;

public class Type extends Word {

	//占用字节数
	private int width;
	
	public static final Type
	INT = new Type("int", Tag.BASIC, 4),
	DOUBLE = new Type("double", Tag.BASIC, 8),
	CHAR = new Type("char", Tag.BASIC, 1),
	BOOL = new Type("bool", Tag.BASIC, 1);
	
	public Type(String lexeme, int tag, int width) {
		super(lexeme, tag);
		this.width = width;
	}
	
	/*
	 * 判断类型是否是char、int、double中的一种，用于类型转换
	 * 
	 * @param type 传入类型
	 * 
	 * @return 是其中一种返回true 否则返回false
	 */
	public static boolean numeric(Type type) {
		if(type == Type.CHAR || type == Type.INT || type == Type.DOUBLE){
			return true;
		}else{
			return false;
		}
	}
	
	/*
	 * 类型转换
	 * 
	 * @param type1 操作数1的类型
	 * 
	 * @param type2 操作数2的类型
	 * 
	 * @return 两个操作数类型的“max”值
	 *
	 */
	public static Type max(Type type1, Type type2){
		if(!numeric(type1) || !numeric(type2)){
			return null;
		}else if(type1 == Type.DOUBLE || type2 == Type.DOUBLE){
			return Type.DOUBLE;
		}else if(type1 == Type.INT || type2 == Type.INT){
			return Type.INT;
		}else{
			return Type.CHAR;
		}
	}
	
	
	public int getWidth() {
		return width;
	}

}
