package parser;

import exception.IdentifierExistException;
import exception.TypeErrorException;
import exception.UndefinedException;
import inter.Id;
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
    //Id暂存栈
    private Stack<Token> mTokenStack;
    //Expr暂存栈
    //保存当前token
    private Token mToken;
    //保存当前正在声明的类型
    private Type mType;
    //保存之前声明的类型
    private Type mPreType;
    //保存当前正在声明Id
    private Word mId;
    //保存当前要归约的数字、bool类型
    private Num mNum;
    private Real mReal;
    private Word mBool;
    private Chart mChart;
    private HashMap<String, String> mParseChart;
    private List<String> mProduction;
    //是否输出移入-归约操作信息
    private boolean isPrintShiftReduce;

    public Parser(Lexer mLexer, Chart chart, boolean isPrintShiftReduce) {
        this.mLexer = mLexer;
        mEnv = null;
        mStatusStack = new Stack<String>();
        mStatusStack.push("0");
        mTokenStack = new Stack<Token>();
        mChart = chart;
        mParseChart = mChart.getmChart();
        mProduction = mChart.getmGrammer().getmProductionRules();
        this.isPrintShiftReduce = isPrintShiftReduce;
    }

    public void parse() throws UndefinedException, TypeErrorException, IdentifierExistException {
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
                //暂存Id和Num
                save();
                token = nextToken();
            }else if(action.charAt(0) == 'r'){
                String production = mProduction.get(Integer.valueOf(action.substring(1)));
                //归约的时候做一些语义动作
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

    public char nextToken() throws TypeErrorException, UndefinedException, IdentifierExistException {
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
        if(isPrintShiftReduce){
            System.out.println(mStatusStack + "\t" + token.toString() + "\t" + action);
        }
    }

    //保存当前类型
    public void saveType() throws TypeErrorException, UndefinedException, IdentifierExistException {
        if(mToken.getTag() == Tag.BASIC){
            mPreType = mType;
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
            if(mPreType == null){
                mPreType = mType;
            }
        }
    }

    //暂存变量和常量
    public void save(){
        switch (mToken.getTag()){
            case Tag.ID:
                mId = (Word) mToken;
                break;
            case Tag.NUM:
                mNum = (Num) mToken;
                break;
            case Tag.REAL:
                mReal = (Real) mToken;
                break;
            case Tag.TRUE:
                mBool = (Word) mToken;
                break;
            case Tag.FALSE:
                mBool = (Word) mToken;
                break;
            default:
                break;
        }
    }
    //在符号表中查找Id
    public Id checkEnv(Word id) throws UndefinedException {
        Env env = mEnv;
        for(;env != null;env = env.getPrEnv()){
            if(env.get(id.getLexeme()) != null){
                return env.get(id.getLexeme());
            }
        }
        if (env == null){
            throw new UndefinedException("\"" + id.getLexeme() + "\" is undefined identifier, error occured near line" + Lexer.line);
        }
        return null;
    }
    //压入栈，并扫描下一个token
    private void pushTokenAndState(Character c) throws TypeErrorException, UndefinedException, IdentifierExistException {
        while (true){
            String state = mStatusStack.peek();
            String action = mParseChart.get(state + c);
            if(action.charAt(0) == 's'){
                printStackTokenAction(c, "移入");
                mStatusStack.push(c.toString());
                mStatusStack.push(action.substring(1));
                save();
                try {
                    mToken = mLexer.scan();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }else if(action.charAt(0) == 'r'){
                String production = mProduction.get(Integer.valueOf(action.substring(1)));
                semantemeAction(Integer.valueOf(action.substring(1)));
                int betaSize = production.length() - 2;
                printStackTokenAction(c, "根据" + production + "归约");
                //如果按产生式3或8归约，由于这两条产生式右部为空所以不需要弹出
                if(!action.substring(1).equals("3") && !action.substring(1).equals("8")){
                    for(int i = 0;i < betaSize*2;i++){
                        mStatusStack.pop();
                    }
                }
                String top = mStatusStack.peek();
                Character character = production.charAt(0);
                mStatusStack.push(character.toString());

                mStatusStack.push(mParseChart.get(top + character.toString()).substring(1));
            }
        }
    }

    //返回0为变量，返回1为整形,
    // 返回2为实型，temp则返回3，
    //4表示类型为bool
    // 都不是则-1
    private int checkClassOf(Token token){
        if(token.getTag() == Tag.ID){
            return 0;
        }else if(token.getTag() == Tag.NUM){
            return 1;
        }else if(token.getTag() == Tag.REAL){
            return 2;
        }else if(token.getTag() == Tag.TEMP){
            return 3;
        }else if(token.getTag() == Tag.TRUE || token.getTag() == Tag.FALSE){
            return 4;
        }
        return -1;
    }

    private Temp printAndPushTemp(Token leftExpr, Token rightExpr, Character op) throws UndefinedException, TypeErrorException {
        Temp temp = null;
        if(checkClassOf(leftExpr) == 0){
            Id leftId = checkEnv((Word) leftExpr);
            System.out.print("<" + op.toString() + " , " + ((Word)leftExpr).getLexeme() + " , ");
            switch (checkClassOf(rightExpr)){
                case 0:
                    Id rightId = checkEnv((Word) rightExpr);
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftId.getType(), rightId.getType()));
                    System.out.print(((Word)rightExpr).getLexeme() + " , ");
                    break;
                case 1:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftId.getType(), Type.INT));
                    System.out.print(((Num)rightExpr).getValue() + " , ");
                    break;
                case 2:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftId.getType(), Type.DOUBLE));
                    System.out.print(((Real)rightExpr).getValue() + " , ");
                    break;
                case 3:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftId.getType(), ((Temp)rightExpr).getmType()));
                    System.out.print("t" + ((Temp)rightExpr).getmNumber() + " , ");
                    break;
                default:
                    throw new TypeErrorException("type error near line" + Lexer.line);
            }
        } else if (checkClassOf(leftExpr) == 1){
            System.out.print("<" + op.toString() + " , " + ((Num)leftExpr).getValue() + " , ");
            switch (checkClassOf(rightExpr)){
                case 0:
                    Id rightId = checkEnv((Word) rightExpr);
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.INT, rightId.getType()));
                    System.out.print(((Word)rightExpr).getLexeme() + " , ");
                    break;
                case 1:
                    temp = new Temp("temp", Tag.TEMP, Type.INT);
                    System.out.print(((Num)rightExpr).getValue() + " , ");
                    break;
                case 2:
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.INT, Type.DOUBLE));
                    System.out.print(((Real)rightExpr).getValue() + " , ");
                    break;
                case 3:
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.INT, ((Temp)rightExpr).getmType()));
                    System.out.print("t" + ((Temp)rightExpr).getmNumber() + " , ");
                    break;
                default:
                    throw new TypeErrorException("type error near line" + Lexer.line);
            }


        } else if (checkClassOf(leftExpr) == 2){
            System.out.print("<" + op.toString() + " , " + ((Real)leftExpr).getValue() + " , ");
            switch (checkClassOf(rightExpr)){
                case 0:
                    Id rightId = checkEnv((Word) rightExpr);
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.DOUBLE, rightId.getType()));
                    System.out.print(((Word)rightExpr).getLexeme() + " , ");
                    break;
                case 1:
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.DOUBLE, Type.INT));
                    System.out.print(((Num)rightExpr).getValue() + " , ");
                    break;
                case 2:
                    temp = new Temp("temp", Tag.TEMP, Type.DOUBLE);
                    System.out.print(((Real)rightExpr).getValue() + " , ");
                    break;
                case 3:
                    temp = new Temp("temp", Tag.TEMP, Type.max(Type.DOUBLE, ((Temp)rightExpr).getmType()));
                    System.out.print("t" + ((Temp)rightExpr).getmNumber() + " , ");
                    break;
                default:
                    throw new TypeErrorException("type error near line" + Lexer.line);
            }

        }else if(checkClassOf(leftExpr) == 3){
            Temp leftTemp = (Temp) leftExpr;
            System.out.print("<" + op.toString() + ", t" + leftTemp.getmNumber() + " , ");
            switch (checkClassOf(rightExpr)){
                case 0:
                    Id rightId = checkEnv((Word) rightExpr);
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftTemp.getmType(), rightId.getType()));
                    System.out.print(((Word)rightExpr).getLexeme() + " , ");
                    break;
                case 1:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftTemp.getmType(), Type.INT));
                    System.out.print(((Num)rightExpr).getValue() + " , ");
                    break;
                case 2:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftTemp.getmType(), Type.DOUBLE));
                    System.out.print(((Real)rightExpr).getValue() + " , ");
                    break;
                case 3:
                    temp = new Temp("temp", Tag.TEMP, Type.max(leftTemp.getmType(), ((Temp)rightExpr).getmType()));
                    System.out.print("t" + ((Temp)rightExpr).getmNumber() + " , ");
                    break;
                default:
                    throw new TypeErrorException("type error near line" + Lexer.line);
            }
        }
        if(temp.getmType() == null){
            throw new TypeErrorException("type error near line" + Lexer.line);
        }
        System.out.println("t" + temp.getmNumber() + ">");
        mTokenStack.push(temp);
        return temp;
    }

    public void semantemeAction(int numOfProduction) throws UndefinedException, TypeErrorException, IdentifierExistException {
        Token rightExpr;
        Token leftExpr;
        switch (numOfProduction){
            case 0:
                break;
            case 4:
                if(mEnv.getTable().get(mId.getLexeme()) != null){
                    throw  new IdentifierExistException("Identifier already exist, error near line" + Lexer.line);
                }
                if(mToken.getTag() != Tag.BASIC){
                    mEnv.put(mId.getLexeme(), new Id(mId, mType));
                }else{
                    mEnv.put(mId.getLexeme(), new Id(mId, mPreType));
                }
                break;
            case 6:

                break;
            case 9:
                rightExpr = mTokenStack.pop();
                leftExpr = mTokenStack.pop();
                equalsExpr(leftExpr, rightExpr);
                break;

            case 17:
                checkEnv(mId);
                mTokenStack.push(mId);
                break;
            case 30:
                rightExpr = mTokenStack.pop();
                leftExpr = mTokenStack.pop();
                printAndPushTemp(leftExpr, rightExpr, '+');
                break;
            case 31:
                rightExpr = mTokenStack.pop();
                leftExpr = mTokenStack.pop();
                printAndPushTemp(leftExpr, rightExpr, '-');
                break;
            case 33:
                rightExpr = mTokenStack.pop();
                leftExpr = mTokenStack.pop();
                printAndPushTemp(leftExpr, rightExpr, '*');
                break;
            case 34:
                rightExpr = mTokenStack.pop();
                leftExpr = mTokenStack.pop();
                printAndPushTemp(leftExpr, rightExpr, '/');
                break;
            case 41:
                mTokenStack.push(mNum);
                break;
            case 42:
                mTokenStack.push(mReal);
                break;
            case 43:
            case 44:
                mTokenStack.push(mBool);
                break;
            default:
                break;
        }
    }

    //赋值表达式归约时执行
    public void equalsExpr(Token leftExpr, Token rightExpr) throws TypeErrorException, UndefinedException {
        if(checkClassOf(leftExpr) != 0){
            throw new TypeErrorException("type error near line" + Lexer.line);
        }
        Id leftId = checkEnv((Word) leftExpr);
        switch (checkClassOf(rightExpr)){
            case 0:
                Id rightId = checkEnv((Word) rightExpr);
                if(Type.max(leftId.getType(),rightId.getType()) == null && leftId.getType() != rightId.getType()){
                    throw new TypeErrorException("type error near line" + Lexer.line);
                }
                System.out.println("<= , " + ((Word)rightExpr).getLexeme() + " , " + " , " + ((Word)leftExpr).getLexeme() + ">");
                break;
            case 1:
                if(Type.max(leftId.getType(),Type.INT) == null){
                    throw new TypeErrorException("type error near line" + Lexer.line);
                }
                System.out.println("<= , " + ((Num)rightExpr).getValue() + " , " + " , " + ((Word)leftExpr).getLexeme() + ">");
                break;
            case 2:
                if(Type.max(leftId.getType(),Type.DOUBLE) == null){
                    throw new TypeErrorException("type error near line" + Lexer.line);
                }
                System.out.println("<= , " + ((Real)rightExpr).getValue() + " , " + " , " + ((Word)leftExpr).getLexeme() + ">");
                break;
            case 3:
                if(Type.max(leftId.getType(),((Temp)rightExpr).getmType()) == null){
                    throw new TypeErrorException("type error near line" + Lexer.line);
                }
                System.out.println("<= , t" + ((Temp)rightExpr).getmNumber() + " , " + " , " + ((Word)leftExpr).getLexeme() + ">");
                break;
            case 4:
                if(leftId.getType() != Type.BOOL){
                    throw new TypeErrorException("type error near line" + Lexer.line);
                }
                System.out.println("<= , " + ((Word)rightExpr).getLexeme() + " , " + " , " + ((Word)leftExpr).getLexeme() + ">");

            default:
                break;
        }
    }
}
