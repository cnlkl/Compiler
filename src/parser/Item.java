package parser;

import java.util.ArrayList;
import java.util.List;

/**
 * 项类
 * Created by 李坤隆 on 2016/12/24.
 */
public class Item implements Cloneable{
    //产生式左部
    private char mLeft;
    //产生式右部
    private String mRight;
    //点所在位置
    private int mPointPosition;
    //First(beta+a)
    private List<Character> a;

    public Item(char mLeft, String mRight, int mPointPosition, List<Character> a) {
        this.mLeft = mLeft;
        this.mRight = mRight;
        this.mPointPosition = mPointPosition;
        this.a = a;
    }


    //-------------------------------Getter And Setter------------------------------
    public char getmLeft() {
        return mLeft;
    }

    public void setmLeft(char mLeft) {
        this.mLeft = mLeft;
    }

    public String getmRight() {
        return mRight;
    }

    public void setmRight(String mRight) {
        this.mRight = mRight;
    }

    public int getmPointPosition() {
        return mPointPosition;
    }

    public void setmPointPosition(int mPointPosition) {
        this.mPointPosition = mPointPosition;
    }

    public List<Character> getA() {
        return a;
    }

    public void setA(List<Character> a) {
        this.a = a;
    }
    //-------------------------------Getter And Setter------------------------------


    public List<Character> firstS(){
        Grammer grammer = Grammer.getInstance();
        String beta = mRight.substring(mPointPosition+1);
        List<Character> characters = new ArrayList<Character>();
        for(int i = 0;i < beta.length();i++){
            if(grammer.isInList(grammer.getmTerminals(), beta.charAt(i))){
            	if(!grammer.isInList(characters, beta.charAt(i))){
            		characters.add(beta.charAt(i));
            	}
                return characters;
            }else{
            	for(Character character : grammer.getmFirstCollection().get(beta.charAt(i))){
            		if(!grammer.isInList(characters, character)){
            			characters.add(character);
            		}
            	}
            	if(!grammer.isInList(grammer.getmNullAbleList(), beta.charAt(i))){
            		return characters;
            	}
            }
        }
        //若beta中全为终结符且都可以为空，则将该项中的前看符号加入到集合中
        for(Character c : a){
            if(!grammer.isInList(characters, c)){
                characters.add(c);
            }
        }
        return characters;
    }
    
    public boolean equals(Item item){
    	
    	if(mLeft == item.getmLeft() && mRight.equals(item.getmRight()) && mPointPosition == item.getmPointPosition()){
    		for(int i = 0;i < a.size();i++){
    			int j = 0;
    		    for(;j < item.getA().size();j++){
    				if(a.get(i) == item.getA().get(j)){
    				    break;
                    }
    			}
    			if(j == item.getA().size()){
    		        return false;
                }
    		}
    		return true;
    	}
    	return false;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
