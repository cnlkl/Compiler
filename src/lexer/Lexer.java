package lexer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;


public class Lexer {
	//源程序行号
	public static int line = 1;
	//用于存储读入的字符
	private char peek = ' ';
	//用于存储保留字
	HashMap<String, Word> words;
	//用于读取文件
	BufferedReader bufferedReader;
	
	public Lexer() throws UnsupportedEncodingException, FileNotFoundException {
		words = new HashMap<String, Word>();
		//存储保留字
		words.put(Word.IF.getLexeme(), Word.IF);
		words.put(Word.ELSE.getLexeme(), Word.ELSE);
		words.put(Word.WHILE.getLexeme(), Word.WHILE);
		words.put(Word.DO.getLexeme(), Word.DO);
		words.put(Word.BREAK.getLexeme(), Word.BREAK);
		words.put(Word.TRUE.getLexeme(), Word.TRUE);
		words.put(Word.FALSE.getLexeme(), Word.FALSE);
		words.put(Type.INT.getLexeme(), Type.INT);
		words.put(Type.CHAR.getLexeme(), Type.CHAR);
		words.put(Type.BOOL.getLexeme(), Type.BOOL);
		words.put(Type.DOUBLE.getLexeme(), Type.DOUBLE);
		//从文件输入
		File file = new File("src\\test.txt");
		InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
		bufferedReader = new BufferedReader(inputStreamReader);
	}
	
	void readCh() throws IOException{
		//读入字符
		peek = (char) bufferedReader.read();
	}
	
	boolean readCh(char c) throws IOException{
		readCh();
		if(peek != c){
			return false;
		}else{
			peek = ' ';
			return true;
		}
	}
	
	public Token scan() throws IOException{
		for(;;readCh()){
			if(peek == ' ' || peek == '\t' || peek == '\r'){
				continue;
			}else if(peek == '\n' ){
				line++;
			}else if(peek == '/'){
				if(readCh('/')){
					do{
						readCh();
					}while(peek != '\n' && peek != '\r');
				}else{
					return new Token('/');
				}
			}else {
				break;
			}
		}
		
		//如果第一个字符是逻辑运算符
		switch (peek) {
		case '&':
			if(readCh('&')){
				return Word.AND;
			}else{
				return new Token('&');
			}
		case '|':
			if(readCh('|')){
				return Word.OR;
			}else{
				return new Token('|');
			}
		case '=':
			if(readCh('=')){
				return Word.EQ;
			}else{
				//赋值作为语句处理，而不是运算符
				return new Token('=');
			}
		case '!':
			if(readCh('=')){
				return Word.NE;
			}else{
				return new Token('!');
			}
		case '<':
			if(readCh('=')){
				return Word.LE;
			}else {
				return new Token('<');
			}
		case '>':
			if(readCh('=')){
				return Word.GE;
			}else {
				return new Token('>');
			}
		}
		
		//如果第一个字符是数字
		if(Character.isDigit(peek)){
			int value = 0;
			do {
				value = 10*value + Character.digit(peek, 10);
				readCh();
			}while(Character.isDigit(peek));
			
			//如果下一个字符不是'.'返回整形Token
			if(peek != '.'){
				return new Num(value);
			} 
			//如果是浮点型
			double x = value;
			double d = 10;
			while(true){
				readCh();
				if(!Character.isDigit(peek)){
					break;
				}else{
					x = x + Character.digit(peek, 10)/d;
					d = d * 10;
				}
			}
			return new Real(x);
		}
		
		
		//如果第一个字符是字母
		if(Character.isLetter(peek)){
			StringBuffer stringBuffer = new StringBuffer();
			do{
				stringBuffer.append(peek);
				readCh();
			}while(Character.isLetterOrDigit(peek));
			String value = stringBuffer.toString();
			//判断是否在保留字里面
			Word word = words.get(value);
			if(word != null){
				//是保留字则直接返回
				return word;
			}else{
				word = new Word(value, Tag.ID);
				//下次碰到相同id则直接从HashMap中返回
				words.put(value, word);
				return word;
			}
		}
		
		//其他符号，eg:+,-,*,;,/,(,)
		Token token = new Token(peek);
		peek = ' ';
		return token;
	}
}
