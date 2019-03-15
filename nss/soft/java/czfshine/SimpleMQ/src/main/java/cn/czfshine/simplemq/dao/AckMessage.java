package cn.czfshine.simplemq.dao;

import java.io.Serializable;

/**确认消息
 * @author:czfshine
 * @date:2018/11/27 10:10
 */

public class AckMessage implements Serializable {

    private int id;
    public AckMessage(int id) {
        this.id = id;
    }
    private static final long serialVersionUID = 4465215196894949029L;
}
