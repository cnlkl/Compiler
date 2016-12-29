package parser;

import lexer.*;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * 驱动类
 * Created by 李坤隆 on 2016/12/25.
 */
public class Parser {
    private Lexer mLexer;
    //符号表
    private Env mEnv;
    //符号状态转移栈
    private Stack<String> mStatusStack;
    //Expr、Id、Op暂存栈
    private Stack<Expr> mTypeStack;
    //保存当前token
    private Token mToken;
    //保存当前正在声明的类型
    private Type mType;
    //保存当前正在声明Id
    private Token mId;
    private Chart mChart;
    private HashMap<String, String> mParseChart;
    private List<String> mProduction;

    public Parser(Lexer mLexer, Chart chart) {
        this.mLexer = mLexer;
        mEnv = null;
        mStatusStack = new Stack<String>();
        mStatusStack.push("0");
        mChart = chart;
        mParseChart = mChart.getmChart();
        mProduction = mChart.getmGrammer().getmProductionRules();
    }

    public void parse(){
        Character token = ' ';
        token = nextToken();
        while (true){
            if(token == 65535){
                token = '$';
            }
            String state = mStatusStack.peek();
            String action = mParseChart.get(state + token.toString());
            if (action == null){
                System.out.println("error near line: " + Lexer.line);
                break;
            }
            if(action.charAt(0) == 's'){
                printStackTokenAction(token, "移入");
                mStatusStack.push(token.toString());
                mStatusStack.push(action.substring(1));
                if(mToken.getTag() == Tag.ID){
                    mId = mToken;
                }
                token = nextToken();
            }else if(action.charAt(0) == 'r'){
                String production = mProduction.get(Integer.valueOf(action.substring(1)));
                semantemeAction(Integer.valueOf(action.substring(1)));
                int betaSize = production.length() - 2;
                printStackTokenAction(token, "根据" + production + "归约");
                //如果按产生式3或8归约，由于这两条产生式右部为空所以不需要弹出
                if(!action.substring(1).equals("3") && !action.substring(1).equals("8")){
                    for(int i = 0;i < betaSize*2;i++){
                        mStatusStack.pop();
                    }
                }
                String top = mStatusStack.peek();
                Character character = production.charAt(0);
                mStatusStack.push(character.toString());
                if(action.charAt(1) == '0'){
                    printStackTokenAction(token, "acc");
                    break;
                }
                mStatusStack.push(mParseChart.get(top + character.toString()).substring(1));
            }
        }
    }

    public char nextToken(){
        char token = ' ';
        try {
            mToken = mLexer.scan();
            saveType();
            token = exchange(mToken);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }

    public char exchange(Token token){
        char result = ' ';

        switch (token.getTag()){
            case Tag.ID:
                result = 'i';
                break;
            case Tag.NUM:
                result = 'm';
                break;
            case Tag.BASIC:
                result = 'b';
                break;
            case Tag.IF:
                result = 'f';
                break;
            case Tag.ELSE:
                result = 's';
                break;
            case Tag.WHILE:
                result = 'w';
                break;
            case Tag.DO:
                result = 'd';
                break;
            case Tag.BREAK:
                result = 'k';
                break;
            case Tag.AND:
                result = '&';
                break;
            case Tag.EQ:
                result = 'e';
                break;
            case Tag.NE:
                result = 'n';
                break;
            case Tag.LE:
                result = 'l';
                break;
            case Tag.GE:
                result = 'g';
                break;
            case Tag.REAL:
                result = 'r';
                break;
            case Tag.TRUE:
                result = 't';
                break;
            case Tag.FALSE:
                result = 'a';
                break;
            case Tag.OR:
                result = 'o';
                break;
            case '{':
                result = '{';
                //新的块则创建属于该块的符号表
                mEnv = new Env(mEnv);
                break;
            default:
                result = token.toString().charAt(0);
                break;
        }

        return result;
    }

    public void printStackTokenAction(Character token, String action){
        System.out.println(mStatusStack + "\t" + token.toString() + "\t" + action);
    }

    //保存当前类型
    public void saveType(){
        if(mToken.getTag() == Tag.BASIC){
            Type typeTemp = (Type) mToken;
            pushTokenAndState(exchange(mToken));
            if(mToken.getTag() == '['){
                pushTokenAndState(exchange(mToken));
                mType = new Array(((Num)mToken).getValue(), typeTemp);
                pushTokenAndState(exchange(mToken));
                pushTokenAndState(exchange(mToken));
            } else{
                mType = typeTemp;
            }
        }
    }

    //压入栈，并扫描下一个token
    private void pushTokenAndState(Character c){
        String state = mStatusStack.peek();
        String action = mParseChart.get(state + c);
        if(action.charAt(0) == 's'){
            printStackTokenAction(c, "移入");
            mStatusStack.push(c.toString());
            mStatusStack.push(action.substring(1));
            try {
                mToken = mLexer.scan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void semantemeAction(int numOfProduction){
        switch (numOfProduction){
            case 0:
                break;
            case 4:
                mEnv.put(((Word)mId).getLexeme(), new Id((Word) mId, mType));
                break;
            case 6:

                break;
            default:
                break;
        }
    }
}
