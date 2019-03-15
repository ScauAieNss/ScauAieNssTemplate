package cn.czfshine.simplemq.client;

import java.io.IOException;
import java.util.Random;

/**生产者客户端.
 * 生产数据并送到消息管道
 * @author:czfshine
 * @date:2018/11/27 9:45
 */

public class ProducerClient {

    BlockServerChannel channel;
    public void start() throws InterruptedException, IOException, ClassNotFoundException {
        //配置看服务端
        channel=new BlockServerChannel("127.0.0.1",23333);
        Random random = new Random();
        for(int i=0;i<10000;i++){
            long res=-1;
            //生产随机数
            //注意：由于消费者要判断是否为素数，
            // 所以生成的随机数要大大一点，而且得随机
            // 有些代码使用int来组合生成long类型的随机数，可能大部分都不是素数
            while(res<5e9){
                res=random.nextLong();
            }
            channel.put(res);

        }

    }

    public static void main(String[] arg) throws InterruptedException, IOException, ClassNotFoundException {
        new ProducerClient().start();
    }
}
