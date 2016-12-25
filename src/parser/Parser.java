package parser;

import lexer.Lexer;
import lexer.Tag;
import lexer.Token;

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
    private Stack<String> mStatusStack;
    private Chart mChart;
    private HashMap<String, String> mParseChart;
    private List<String> mProduction;
    ////////////////////
//    private BufferedReader bufferedReader;

    public Parser(Lexer mLexer, Chart chart) {
        this.mLexer = mLexer;
        mStatusStack = new Stack<String>();
        mStatusStack.push("0");
        mChart = chart;
        mParseChart = mChart.getmChart();
        mProduction = mChart.getmGrammer().getmProductionRules();

        ////////////////////////////////////////
//        File file = new File("src\\test.txt");
//        InputStreamReader inputStreamReader = null;
//        try {
//            inputStreamReader = new InputStreamReader(new FileInputStream(file), "utf-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        bufferedReader = new BufferedReader(inputStreamReader);
        ///////////////////////////////////////
    }

    public void parse(){
        boolean isFirst = true;
        Character token = ' ';
        Character nextToken = ' ';
        Character nullToken = ' ';
        String temp;
        while (true){
            if(isFirst){
                token = nextToken();
                nextToken = nextToken();
                isFirst = false;
            }

            if(token == 65535){
                token = '$';
            }
            String state = mStatusStack.peek();
            //自动输入串
            String nullStr = mParseChart.get(state + "#");
            String action = mParseChart.get(state + token.toString());
            //如果action为null，且当输入为#时可以移进，则将token设置为#
            temp = action;
            if(action == null && nullStr != null && nullStr.charAt(0) == 's'){
                action = nullStr;
                nullToken = token;
                token = '#';
            }
            if (action == null && nullStr == null){
//                System.out.print(mStatusStack + "\t");
//                System.out.print("当前输入:" + token + "\t下个输入:" + nextToken + "\t");
                System.out.println("error near line: " + Lexer.line);
                break;
            }
            if(action.charAt(0) == 's'){
                System.out.print(mStatusStack + "\t");
                System.out.print("当前输入:" + token + "\t下个输入:" + nextToken + "\t");
                System.out.println("移入");
                mStatusStack.push(token.toString());
                mStatusStack.push(action.substring(1));
                if(nullStr == null || nullStr.charAt(0) != 's' || temp != null){
                    token = nextToken;
                    nextToken = nextToken();
                }else{
                    token = nullToken;
                }
            }else if(action.charAt(0) == 'r'){
                if(action.charAt(1) == '0'){
                    System.out.println("acc");
                    break;
                }
                String production = mProduction.get(Integer.valueOf(action.substring(1)));
                System.out.print(mStatusStack + "\t");
                System.out.print("当前输入:" + token + "\t下个输入:" + nextToken + "\t");
                System.out.println("根据" + production + "归约");
                int betaSize = production.length() - 2;
                betaSize *= 2;
                for(int i = 0;i < betaSize;i++){
                    mStatusStack.pop();
                }
                String top = mStatusStack.peek();
                Character character = production.charAt(0);
                mStatusStack.push(character.toString());
                mStatusStack.push(mParseChart.get(top + character.toString()).substring(1));
            }

        }


    }

    public char nextToken(){
        char token = ' ';
        Token t = null;
        try {
            t = mLexer.scan();
            token = exchange(t);
//            token = (char) bufferedReader.read();
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
            default:
                result = token.toString().charAt(0);
                break;
        }

        return result;
    }


}
