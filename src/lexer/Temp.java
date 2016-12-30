package lexer;

/**
 * Created by 李坤隆 on 2016/12/30.
 */
public class Temp extends Word{

    //临时变量个数
    public static int count = 0;
    //当前为第几个临时变量
    private int mNumber;
    //该临时变量类型
    private Type mType;

    public Temp(String lexeme, int tag, Type type) {
        super(lexeme, tag);
        mType = type;
        mNumber = count++;
    }

    public Type getmType() {
        return mType;
    }

    public int getmNumber() {
        return mNumber;
    }
}
