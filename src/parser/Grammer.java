package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 李坤隆 on 2016/12/24.
 */
public class Grammer {
    //终结符列表
    private List<Character> mTerminals;

    //非终结符列表
    private List<Character> mNonTerminals;

    //可推出空的非终结符
    private List<Character> mNullAbleList;

    //first集合
    private Map<Character, List<Character>> mFirstCollection;

    //产生式
    private List<String> mProductionRules;

    public static Grammer mInstance = null;

    private Grammer(String grammer) {
        mTerminals = new ArrayList<Character>();
        mTerminals.add('$');
        mNonTerminals = new ArrayList<Character>();
        mNullAbleList = new ArrayList<Character>();
        mFirstCollection = new HashMap<Character, List<Character>>();
        mProductionRules = new ArrayList<String>();
        initGrammer(grammer);
        initNullAbleList();
        initFirst();
    }

    public static Grammer getInstance(String grammer){
        if(mInstance == null){
            mInstance = new Grammer(grammer);
        }
        return mInstance;
    }

    public static Grammer getInstance(){
        return mInstance;
    }

    //-------------------Getter And Setter Begin--------------
    public List<Character> getmTerminals() {
        return mTerminals;
    }

    public void setmTerminals(List<Character> mTerminals) {
        this.mTerminals = mTerminals;
    }

    public List<Character> getmNonTerminals() {
        return mNonTerminals;
    }

    public void setmNonTerminals(List<Character> mNonTerminals) {
        this.mNonTerminals = mNonTerminals;
    }

    public List<Character> getmNullAbleList() {
        return mNullAbleList;
    }

    public void setmNullAbleList(List<Character> mNullAbleList) {
        this.mNullAbleList = mNullAbleList;
    }

    public Map<Character, List<Character>> getmFirstCollection() {
        return mFirstCollection;
    }

    public void setmFirstCollection(Map<Character, List<Character>> mFirstCollection) {
        this.mFirstCollection = mFirstCollection;
    }

    public List<String> getmProductionRules() {
        return mProductionRules;
    }

    public void setmProductionRules(List<String> mProductionRules) {
        this.mProductionRules = mProductionRules;
    }
    //-------------------Getter And Setter End--------------


    //处理输入的原始文法
    public void initGrammer(String grammer){
        //初始化终结符和非终结符列表
        initTerminalAndNonTerminal(grammer);

        //根据输入文法以分号分隔开
        String[] temp1 = grammer.split(",");
        for(int i = 0;i < temp1.length;i++){
            //分离出产生式左部,":"分隔
            String[] temp = temp1[i].split(":");
            String left = temp[0];
            //产生式右部以"|"分隔
            String[] right = temp[1].split("\\|");
            for(int j = 0;j < right.length;j++){
                mProductionRules.add(left + ":" + right[j]);
            }
        }
    }

    //初始化终结符和非终结符列表
    public void initTerminalAndNonTerminal(String grammer){
        for(int i = 0;i < grammer.length();i++){
            char t = grammer.charAt(i);
            if(Character.isUpperCase(t)){
                if(!isInList(mNonTerminals, t)){
                    mNonTerminals.add(t);
                }
            }else if(t != '|' && t != ':' && t != ','){
                if(!isInList(mTerminals, t)){
                    mTerminals.add(t);
                }
            }
        }
    }

    //判断t是否在list中
    public boolean isInList(List<Character> list, char t){
        for(Character c : list){
            if(c == t){
                return true;
            }
        }
        return false;
    }

    public void initNullAbleList(){
        //标记集合是否发生改变
        boolean tag = true;
        while (tag){
            tag = false;
            for(String production : mProductionRules){
                String[] temp = production.split(":");
                if(temp[1].equals("#") && !isInList(mNullAbleList, temp[0].charAt(0))){
                    mNullAbleList.add(temp[0].charAt(0));
                    tag = true;
                }else {
                    int i = 0;
                    for(;i < temp[1].length();i++){
                        //如果是非终结符
                        if(isInList(mNonTerminals, temp[1].charAt(i))){
                            if(isInList(mNullAbleList, temp[1].charAt(i))){
                                continue;
                            }else{
                                break;
                            }
                        }else{
                            break;
                        }
                    }
                    if(i == temp[1].length() && !isInList(mNullAbleList, temp[0].charAt(0))){
                        mNullAbleList.add(temp[0].charAt(0));
                        tag = true;
                    }
                }

            }
        }
    }

    //求每个非终结符first集
    public void initFirst(){

        for(Character c : mNonTerminals){
            mFirstCollection.put(c, new ArrayList<Character>());
        }
        	
        boolean tag = true;
        while (tag){
            tag = false;
            for (String production : mProductionRules){
            	String[] temp = production.split(":");
            	char productionLeft = temp[0].charAt(0);
            	String productionRight = temp[1];
            	for(int i = 0;i < productionRight.length();i++){
            		//终结符则添加到first集里面
            	    if(isInList(mTerminals, productionRight.charAt(i))){
            			if(!isInList(mFirstCollection.get(productionLeft), productionRight.charAt(i))){
            				mFirstCollection.get(productionLeft).add(productionRight.charAt(i));
            				tag = true;            				
            			}
            			break;
            		}else{
            		    //非终结符则合并两个的first集
            			for(Character c : mFirstCollection.get(productionRight.charAt(i))){
            				if(!isInList(mFirstCollection.get(productionLeft), c)){
            					mFirstCollection.get(productionLeft).add(c);
            					tag = true;
            				}
            			}
            			if(!isInList(mNullAbleList, productionRight.charAt(i))){
            				break;
            			}
            		}
            	}
            }
        }
    }

    public void printAllProductionRules(){
        int count = 0;
        System.out.println("产生式规则");
        for (String prodcution : mProductionRules){
            System.out.println(count++ + ": " + prodcution);
        }
    }
}
