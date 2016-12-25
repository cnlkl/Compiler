package lexer;

public class Array extends Type {

	private Type type;
	
	private int size = 1;
	
	public Array(int size, Type type) {
		super("[]", Tag.INDEX, size * type.getWidth());
		this.size = size;
		this.type = type;
	}
	
	public String toString() {
		return "[" + size + "]" + type.toString();
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	
}
