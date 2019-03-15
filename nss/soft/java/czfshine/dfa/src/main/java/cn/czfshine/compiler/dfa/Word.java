package cn.czfshine.compiler.dfa;

/**
 * @author:czfshine
 * @date:2018/11/21 19:41
 */

public class Word {
    public Word(String name) {
        this.name = name;
    }

    private String name;

    private String type;

    public String getName() {
        return name;
    }
}
