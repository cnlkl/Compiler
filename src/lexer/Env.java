package lexer;

import java.util.Hashtable;

public class Env {
	private Hashtable<Token, Id> table;
	protected Env prEnv;
	
	public Env(Env v) {
		table = new Hashtable<Token, Id>();
		prEnv = v;
	}
	
	public void put(Token word, Id id) {
		table.put(word, id);
	}
	
	public Id get(Token word){
		for(Env env = this;env != null;env = env.prEnv){
			Id found = env.table.get(word);
			if(found != null){
				return found;
			}
		}
		return null;
	}
}
