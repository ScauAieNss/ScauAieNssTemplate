package cn.czfshine.compiler.dfa;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:czfshine
 * @date:2018/11/21 19:40
 */

public class WordTable {

    private List<Word> allword = new ArrayList<>();
    public int push(String word){
        allword.add(new Word(word));
        return allword.size()-1;
    }

    private int temno=0;
    public int createTempWord(){
        String wordname="t"+temno;
        temno++;
        allword.add(new Word(wordname));
        return allword.size()-1;
    }

    public Word getWord(int index){
        return allword.get(index);
    }
}
