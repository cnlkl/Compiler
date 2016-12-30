package inter;

import lexer.Type;
import lexer.Word;

//变量作为表达式
public class Id extends Expr {
	private int offset;

	
	/**
	 * 
	 * @param id 用于获取变量名
	 * @param type 变量类型
	 * @param b 变量地址 
	 */
	public Id(Word id, Type type) {
		super(id, type);
	}


	public int getOffset() {
		return offset;
	}


	public void setOffset(int offset) {
		this.offset = offset;
	}

}
