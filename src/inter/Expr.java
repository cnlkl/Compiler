package inter;


import lexer.Token;
import lexer.Type;

public class Expr extends Node {
	
	protected Token op;
	protected Type type;
	
	public Expr(Token op, Type type) {
		this.op = op;
		this.type = type;
	}
	
	public Token getOp() {
		return op;
	}

	public void setOp(Token op) {
		this.op = op;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	
	
	public Expr gen() {
		return this;
	}
	
	//规约函数
	public Expr reduce() {
		return this;
	}
	
	
	/**
	 * bool表达式生成跳转代码
	 * 
	 * @param test bool表达式
	 * @param t 值为真的跳转语句标号，特殊标号0表示跳过
	 * @param f 值为假的跳转语句标号，特殊标号0表示跳过
	 */
	public void emitJumps(String test, int t, int f) {
		if(t != 0 && f != 0){
			emit("if " + test + " goto L" + t);
			emit("goto L" + f);
		}else if(t != 0) {
			emit("if " + test + "goto L" + t);
		}else if(f != 0) {
			emit("iffalse" + test + " goto L" + f);
		}
	}
	
	public void jumping(int t, int f) {
		emitJumps(toString(), t, f);;
	}

	@Override
	public String toString() {
		return op.toString();
	}

}
