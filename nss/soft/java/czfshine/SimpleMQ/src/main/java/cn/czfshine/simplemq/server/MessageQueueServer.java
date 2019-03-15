package cn.czfshine.simplemq.server;

import cn.czfshine.simplemq.dao.AckMessage;
import cn.czfshine.simplemq.dao.Product;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;

import cn.czfshine.simplemq.dao.RequestMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 消息队列服务器.
 * 为了方便实现，每个端口暂时使用一个线程进行处理
 * todo:多线程socket
 * @author:czfshine
 * @date:2018/11/26 13:06
 */

public class MessageQueueServer {


    private ServerSocket pSocket;
    private ServerSocket cSocket;

    private int pport = 23333; //生产者服务监听端口
    private int cport = 6666;  //消费者监听服务端口

    public void start() {
        startListen();
    }

    //模拟阻塞的消息队列
    BlockingQueue<Long> blockingQueue = new ArrayBlockingQueue<>(10);


    /**
     * 生产者服务监听器
     */
    class PListener implements Runnable {
        @Override
        public void run() {
            try {
                Logger logger = LogManager.getLogger("Pserver");

                //监听的socket端口
                pSocket = new ServerSocket(pport, 1);

                logger.info("Server start");
                while (true) {

                    Socket ps = pSocket.accept();
                    logger.info("Some connect");

                    InputStream inputStream = ps.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(inputStream);
                    ObjectOutputStream oos = new ObjectOutputStream(ps.getOutputStream());

                    Product p;

                    while (true) {

                        try {
                            //如果接收到发来的数据
                            p = (Product) ois.readObject();
                            logger.info("accept product" + p);
                            try {
                                //加入队列（阻塞的）
                                blockingQueue.put(p.getData());
                                //成功返回回调消息
                                oos.writeObject(new AckMessage(p.getId()));
                                oos.flush();
                                logger.info("put&Ack" + p);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 生产者服务监听器
     */
    class CListener implements Runnable {
        @Override
        public void run() {
            try {
                Logger logger = LogManager.getLogger("Cserver");
                cSocket = new ServerSocket(cport, 1);

                logger.info("Server start");

                while (true) {

                    Socket cs = cSocket.accept();
                    logger.info("Some connect");

                    InputStream inputStream = cs.getInputStream();
                    ObjectInputStream ois = new ObjectInputStream(inputStream);
                    ObjectOutputStream oos = new ObjectOutputStream(cs.getOutputStream());
                    RequestMessage rm;
                    while (true) {

                        try {
                            //收到拉取请求
                            rm = (RequestMessage) ois.readObject();
                            logger.info("accept request " + rm);
                            try {
                                Product res;
                                //尝试从队列取数据并回调
                                long i = blockingQueue.take();
                                res = new Product(rm.getId(), i, 1);
                                oos.writeObject(res);
                                oos.flush();
                                logger.info("tack&Ack" + res);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }


                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void startListen() {

        new Thread(new PListener()).start();
        new Thread(new CListener()).start();
    }

    public static void main(String arg[]) {
        new MessageQueueServer().start();
    }
}
