package cn.czfshine.simplemq.dao;

import java.io.Serializable;

/**请求消息
 * @author:czfshine
 * @date:2018/11/27 15:04
 */

public class RequestMessage implements Serializable {

    private int id;

    public RequestMessage(int id) {
        this.id = id;
    }

    private static final long serialVersionUID = 666159337765996411L;

    public int getId() {
        return id;
    }
}
