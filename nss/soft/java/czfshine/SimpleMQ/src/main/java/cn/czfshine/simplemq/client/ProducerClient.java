package cn.czfshine.simplemq.client;

import java.util.Random;

/**
 * @author:czfshine
 * @date:2018/11/27 9:45
 */

public class ProducerClient {

    BlockServerChannel channel;
    public void start() throws InterruptedException {
        channel=new BlockServerChannel("127.0.0.1",23333);
        Random random = new Random();
        for(int i=0;i<10000;i++){
            long res=-1;

            while(res<5e9){
                res=random.nextLong();
            }
            channel.put((long) res);

        }

    }

    public static void main(String[] arg) throws InterruptedException {
        new ProducerClient().start();
    }
}
