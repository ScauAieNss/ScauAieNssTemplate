package cn.czfshine.compiler.dfa;

public class Token{
    public Token(int type, String data,int line) {
        this.type = type;
        this.data = data;
        this.line=line;
    }

    private int type;
    private String data;
    private int line;

    public boolean isId(){
        return type==10 || type==11 ;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof String){
            String s=(String )o;
            return s.equals(data);
        }
        return false;
    }
    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", data='" + data + '\'' +
                '}';
    }

    public int getLine() {
        return line;
    }
    public String getData(){
        return data;
    }
}