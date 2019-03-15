package cn.czfshine.simplemq.dao;

import java.io.Serializable;

/**商品对象--生产者生产，消费者消费的
 * @author:czfshine
 * @date:2018/11/27 10:10
 */

public class Product implements Serializable {

    int id;
    long data;
    int type;

    public int getId() {
        return id;
    }

    public long getData() {
        return data;
    }

    public int getType() {
        return type;
    }

    public Product(int id, long data, int type) {

        this.id = id;
        this.data = data;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", data=" + data +
                ", type=" + type +
                '}';
    }

    private static final long serialVersionUID = -7878220743276452576L;
}
