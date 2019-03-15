package cn.czfshine.compiler.dfa;

import java.io.*;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * @author:czfshine
 * @date:2018/10/24 18:10
 */

public class SimplePaser {

    class PaserException extends Exception{
        private String msg;
        private int line;
        public PaserException(int line,String msg){
            this.msg=msg;
            this.line=line;
        }

        public String getMsg() {
            return msg;
        }
    }

    private WordTable wordtable=new WordTable();
    private List<Token> allToken;
    Token curToken;
    int curi;
    Token EOFtoken=new Token(-1,"\0",-1);
    Token nextToken() {
        curi++;
        if(curi<allToken.size()){
            return allToken.get(curi);
        }else{
            return EOFtoken;
        }
    }

    String ID() throws PaserException {
        if(curToken.isId()){
            Token u=curToken;
            curToken=nextToken();
            return u.getData();
        }else{
            //System.out.println(curToken);
            if(curToken==EOFtoken){
                throw  new PaserException(curToken.getLine(),"需要一个标识符，但是串已经结束");
            }
            throw  new PaserException(curToken.getLine(),"需要一个标识符而不是"+curToken);

        }
    }
    void match(String s) throws PaserException {
        if(curToken.equals(s)){
            curToken=nextToken();
        }else{
            throw  new PaserException(curToken.getLine(),"这里需要"+s+"但是当前字符是"+curToken.toString());
        }
    }

    /*语句块返回代码地址*/
    int block() throws PaserException {
        int startidx=getidx();
        while(!curToken.equals("end"))
            SS();
        return startidx;
    }
    void P() throws PaserException {
        match("begin");

        block();

        match("end");
        Ops.ShowAll();
    }
    void SS() throws PaserException {
        if(curToken.equals("if")){
            ifst();
        }else if(curToken.equals("while")){
            loopst();
        }else{
        S();
    }


    }

    int getidx(){
        return Ops.all.size()-1;
    }
    List<String> if_follow_not= new ArrayList<String>();
    {
        if_follow_not.add("end");
        if_follow_not.add("else");
    }

    int ifthenst() throws PaserException {
        int s=getidx();
        while(!if_follow_not.contains(curToken.getData())){
            SS();
        }
        return s;
    }
    String con() throws PaserException {
        return E();
    }
    void ifst() throws PaserException {


        match("if");
        String A=con();
        Ops ifop= new Ops("if");
        Ops gotoa = new Ops("goto");
        Ops gotob = null;
        ifop.setA(A);
        ifop.setRes(((Integer) (getidx()+1)).toString());
        match("then");
        Integer B =ifthenst();
        ifop.setRes(B.toString());

        int c=-1;
        int f=0;
        if(curToken.equals("else")){
            gotob=new Ops("goto");
            f=1;
            match("else");
            c=block();

        }
        match("end");

        if(f==1){
            gotoa.setRes(c);
            gotob.setRes(getidx());
        }else{
            gotoa.setRes(getidx());
        }


    }
    void loopst() throws PaserException {
        match("while");
        con();
        match("do");
        block();
        match("end");
    }
    String curname;
    void S() throws PaserException{

        String name =ID();
        curname=name;
        match("=");

        E(name);
        //todo


    }

    String E(String name) throws PaserException {
        String a=T();
        String b=E_();

        if(b.equals("")){
//            if(Ops.all.size()>0){
//                Ops ops = Ops.all.get(Ops.all.size() - 1);
//                if(ops.hasOp()) {
//                    if(ops.getRes().startsWith("t")){
//                        ops.setRes(name);
//                        return "";
//                    }
//
//                }
//            }
            new Ops(name,a);

            return a;
        }else{
            if(b.startsWith("-")){
                new Ops(name,a,"-",b.substring(1));
            }else{
                new Ops(name,a,"+",b.substring(1));
            }
        }
        return name;
    }
    String E() throws PaserException {
        int c=wordtable.createTempWord();
        String res =wordtable.getWord(c).getName();
        String a=T();
        String b=E_();

        if(b.equals("")){
            return a;
        }else{
            if(b.startsWith("-")){
                new Ops(res,a,"-",b.substring(1));
            }else{
                new Ops(res,a,"+",b.substring(1));
            }
        }
        return res;
    }
    String T() throws PaserException {
        int c=wordtable.createTempWord();
        String res =wordtable.getWord(c).getName();

        String a=F();
        String b=T_();

        if(b.equals("")){
            return a;
        }else{
            new Ops(res,a,"*",b);
        }
        return res;
    }

    String F() throws PaserException {
        if(curToken.equals("(")){
            match("(");
            String res=E();
            match(")");
            return res;
        }else{
            return ID();
        }
    }

    String  E_() throws PaserException {
        String op;
        if(curToken.equals("+")){
            match("+");
            op="+";
        }else if(curToken.equals("-")){
            match("-");
            op="-";
        }else{
            return "";
        }
        int c=wordtable.createTempWord();
        String res =wordtable.getWord(c).getName();

        String a=T();
        String b=E_();

        if(b.equals("")){
            return op+a;
        }else{
            if(b.startsWith("-")){
                new Ops(res,a,"-",b.substring(1));
            }else{
                new Ops(res,a,"+",b.substring(1));
            }
        }
        return op+res;
    }
    String  T_() throws PaserException {
        if(curToken.equals("*")){
            match("*");

            int c=wordtable.createTempWord();
            String res =wordtable.getWord(c).getName();

            String a=F();
            String b=T_();

            if(b.equals("")){
                return a;
            }else{
                new Ops(res,a,"*",b);
                return res;
            }

        }
        return "";
    }
    private boolean fail=false;
    public void paser(String code) throws SimpleDFA.NotAcceptException, PaserException {
        allToken = new SimpleDFA().getToken(code);
        //System.out.println(allToken);
        curi=0;
        curToken=allToken.get(curi);
        while(curToken!=EOFtoken){
            try {
                P();
                //System.out.println("解析成功");
            }catch (PaserException e){
                //System.out.println(new SimpleDFA().getToken(code));
                e.printStackTrace();
                fail=true;
                System.out.println("解析失败,语法分析错误");
                System.out.println("在第"+(e.line+1)+"行左右出现错误。");
                System.out.println("错误消息为："+e.getMsg());
                Token old=curToken;
                while(curToken.getLine()==old.getLine() && curToken !=EOFtoken)
                curToken=nextToken();
            }catch (Exception e){
                //System.out.println(new SimpleDFA().getToken(code));
                System.out.println("解析"+code+"失败");
                System.out.println(e);
            }
        }
//
    }

    public static void main(String[] a) throws SimpleDFA.NotAcceptException, PaserException, IOException {
        System.out.println("输入文件名：");
        String filename=new Scanner(System.in).nextLine();
        File file = new File(filename    );
        char [] buff=new char[((Long)file.length()).intValue()];
        int read = new BufferedReader(new InputStreamReader(new FileInputStream(file))).read(buff);
        String code = new String(buff);
        SimplePaser simplePaser = new SimplePaser();
        simplePaser.paser(code);
        
        if(simplePaser.fail==false){
            System.out.println("解析成功");
        }

        //todo:1.正负数
        //todo:2.dfa 优化a=1
        //todo:3 最后一个token缺失


    }
}
