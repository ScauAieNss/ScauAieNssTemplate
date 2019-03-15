package cn.czfshine.compiler.dfa;

import java.util.*;

public class SimpleDFA {
    abstract class States {
        public String name;

        abstract States nextStates(String s) throws  NotAcceptException;

        public String getName(){
            return name;
        }
        public States(){
            init();
        }
        abstract void init();
    }
    abstract class PureStates extends States{}

    abstract class EndStates extends States{

    }
    class NotAcceptException extends Exception {

    }

    private Map<String, DFA.States> startStatesSet = new HashMap<>();
    private Map<String, DFA.States> allStates = new HashMap<>();
    private Set<DFA.States> endStates = new HashSet<>();
    /*状态列表*/
    /* nil,next
    op,ops,ope,opee,
    innum,num,
    inword,word,letter,keyword*/

    private States nil;
    private States inword;
    private States innum;
    private States inxiao;
    private States xiao;
    private States op;
    private States word;
    private States keyword;
    private States letter;
    private States num;
    private States oping;
    private States opinl;
    private States opge;
    private States opg;
    private States ople;
    private States opl;
    private States ope;

    private States space;
    {
        space=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.equals("\n"))
                    line++;
                return nil;
            }

            @Override
            void init() {
                name="space";
            }
        };
        nil=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {

                if(s.matches("[a-zA-Z]")){
                    return inword;
                }
                if(s.matches("[0-9]")){
                    return innum;
                }
                if("=+-*/();:#".contains(s)){
                    return op;
                }
                if(s.equals(">")){
                    return oping;
                }
                if(s.equals("<")){
                    return opinl;
                }
                if(s.matches("[ \t\n\r]")){
                    return space;
                }
                System.out.println(s);
                throw new NotAcceptException();
            }

            @Override
            void init() {
                name="nil";
            }
        };
        inword=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.matches("[a-zA-Z]")){
                    return inword;
                }
                return word;
            }

            @Override
            void init() {
                name="inword";
            }
        };
        innum=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.matches("[0-9]")){
                    return innum;
                }else if(s.equals(".")){
                    return inxiao;
                }
                return num;
            }

            @Override
            void init() {
                name="innum";
            }
        };
        inxiao=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.matches("[0-9]")){
                    return inxiao;
                }
                return xiao;
            }

            @Override
            void init() {
                name="inxiao";
            }
        };
        word=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }
            @Override
            void init() {
                name="word";
            }
        };

        num=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }
            @Override
            void init() {
                name="Num";
            }
        };

        xiao=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }
            @Override
            void init() {
                name="xiao";
            }
        };
        op=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return space;
            }

            @Override
            void init() {
                name="op";
            }
        };
        oping=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.equals("=")){
                    return opge;
                }
                return opg;
            }
            @Override
            void init() {
                name="oping";
            }
        };
        opge=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }

            @Override
            void init() {
                name="opge";
            }
        };
        opg=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return space;
            }

            @Override
            void init() {
                name="opg";
            }
        };

        opinl=new PureStates() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                if(s.equals("=")){
                    return ople;
                }
                if(s.equals(">")){
                    return ope;
                }
                return opl;
            }

            @Override
            void init() {
                name="opinl";
            }
        };
        opl=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return space;
            }

            @Override
            void init() {
                name="opl";
            }
        };
        ople=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }

            @Override
            void init() {
                name="ople";
            }
        };

        ope=new States() {
            @Override
            States nextStates(String s) throws NotAcceptException {
                return nil;
            }

            @Override
            void init() {
                name="ope";
            }
        };
    }

    private Map<String,Integer> keywords=new HashMap<>();
    {
        keywords.put("begin",1);
        keywords.put("if",2);
        keywords.put("then",3);
        keywords.put("while",4);
        keywords.put("do",5);
        keywords.put("end",6);
    }

    private Map<String,Integer> ops=new HashMap<>();
    {
        ops.put("+",13);
        ops.put("-",14);
        ops.put("*",15);
        ops.put("/",16);
        ops.put(":",17);
        ops.put("=",25);
        ops.put(";",26);
        ops.put("(",27);
        ops.put(")",28);
        ops.put("#",0);
    }


    public Token getWordToken(String word){
        int id=keywords.getOrDefault(word,10);
        return new Token(id,word,line);
    }

    public Token getOperateToken(String op){
        int id=ops.getOrDefault(op,-1);
        return new Token(id,op,line);
    }

    int line=0;
    public List<Token> getToken(String code) throws NotAcceptException {
        States cur=nil;
        int i=0;

        StringBuilder wordcache=new StringBuilder();

        List<Token> res=new LinkedList<>();
        while(i<code.length()){
            String curstr=String.valueOf(code.charAt(i));

            States next= cur.nextStates(curstr);

            //System.out.println("当前状态："+cur.getName()+"\t当前字符："+curstr+"\t下个状态："+next.getName());

            if(next==inword){
                wordcache.append(curstr);
                i++;
                cur=next;
                continue;
            }
            if(next==word){
                res.add(getWordToken(wordcache.toString()));
                wordcache.setLength(0);
                cur=next;
                continue;
            }
            if(next==innum){
                wordcache.append(curstr);
                i++;
                cur=next;
                continue;
            }
            if(next==num){
                res.add(new Token(11,wordcache.toString(),line));
                wordcache.setLength(0);
                cur=next;
                continue;
            }

            if(next==inxiao){
                wordcache.append(curstr);
                i++;
                cur=next;
                continue;
            }
            if(next==xiao){
                res.add(new Token(11,wordcache.toString(),line));
                wordcache.setLength(0);
                cur=next;
                continue;
            }

            if(next==op){
                res.add(getOperateToken(curstr));
                wordcache.setLength(0);
                cur=next;
                continue;
            }
            if(next==opge){
                res.add(new Token(24,">=",line));
                cur=next;
                continue;
            }
            if(next==ople){
                res.add(new Token(22,"<=",line));
                cur=next;
                continue;
            }

            if(next==opl){
                res.add(new Token(20,"<",line));
                cur=next;
                continue;
            }
            if(next==opg){
                res.add(new Token(23,">",line));
                cur=next;
                continue;
            }
            if(next==ope){
                res.add(new Token(21,"<>",line));
                i++;

                cur=next;
                continue;
            }

            if(next==nil){
                //i++;
                cur=next;
                continue;
            }
            if(next==space){
                //System.out.println("space"+curstr);
                cur=next;
                i++;
                continue;
            }
            System.out.println("未处理");
            i++;
            cur=next;
        }

        return res;

    }

    public static void main(String[] argv) throws NotAcceptException {
        String code="begin \n" +
                "    a=1;\n" +
                "    s=0; \n" +
                "    while a<10 do\n" +
                "        s=s+a;\n" +
                "        a=a+1;\n" +
                "     end\n" +
                "     if s <> 55 then\n" +
                "           s=0\n" +
                "     end\n" +
                "end    ";

        List<Token> res = new SimpleDFA().getToken(code);

        System.out.println(res);
        System.out.println(res.size());
        for(int j=0;j<res.size();j++){
            System.out.println(res.get(j).toString());
        }
    }
}
