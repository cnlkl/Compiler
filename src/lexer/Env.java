package lexer;

import inter.Id;

import java.util.Hashtable;

public class Env {
	private Hashtable<String, Id> table;
	protected Env prEnv;
	
	public Env(Env v) {
		table = new Hashtable<String, Id>();
		prEnv = v;
	}
	
	public void put(String lexeme, Id id) {
		table.put(lexeme, id);
	}
	
	public Id get(String lexeme){
		for(Env env = this;env != null;env = env.prEnv){
			Id found = env.table.get(lexeme);
			if(found != null){
				return found;
			}
		}
		return null;
	}

	public Env getPrEnv() {
		return prEnv;
	}

	public Hashtable<String, Id> getTable() {
		return table;
	}
}
