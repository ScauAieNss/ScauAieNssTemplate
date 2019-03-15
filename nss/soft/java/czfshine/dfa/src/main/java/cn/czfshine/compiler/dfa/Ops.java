package cn.czfshine.compiler.dfa;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:czfshine
 * @date:2018/11/21 19:46
 */

public class Ops {
    public void setA(String a) {
        this.a = a;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public void setB(String b) {
        this.b = b;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void setAll(List<Ops> all) {
        Ops.all = all;
    }

    public String getA() {
        return a;
    }

    public String getOp() {
        return op;
    }

    public String getB() {
        return b;
    }

    public int getId() {
        return id;
    }

    public static List<Ops> getAll() {
        return all;
    }

    /*                   op    A  res
        if con goto idx  if   con idx
        goto idx         goto     idx

        if con then       if con then
        block1              block
        else               end
        block2
        end

        |                 |
        v                 v

        0:if con goto 2   0: if con goto 2
        1:goto 9*         1: goto 9*
        2:block1          block
        ...               9:..
        7:block1end
        8:goto 13*
        9:block2
        ...
        12:block2end
        13:..



        while con do
            block
        end

        0:if !con goto 9*
        block
        8:goto 0
        9:..




     */


    public void setRes(String res) {
        this.res = res;
    }
    public  void setRes(int res){
        this.res=((Integer) res).toString();
    }

    public String getRes() {
        return res;
    }

    private String res;
    private String a;
    private String op;
    private String b;
    private int id;

    public static List<Ops> all = new ArrayList<>();
    public Ops(){
        this.id=all.size();
        all.add(this);
    }
    public Ops(String res, String a, String op, String b) {
        this();
        this.res = res;
        this.a = a;
        this.op = op;
        this.b = b;

    }


    public Ops(String res, String a) {
        this();
        this.res = res;
        this.a = a;

    }
    public Ops(String op){
        this();
        this.setOp(op);
    }

    public static void ShowAll(){
        for (Ops o: all
             ) {

            System.out.println(o.toString());
        }
    }
    @Override
    public String toString() {
        if(op!=null){

            return res+"="+a+op+b;
        }
        return res+"="+a;

    }
    public boolean hasOp(){
        return op!=null;
    }
}
