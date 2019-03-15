package cn.czfshine.simplemq.client;

import cn.czfshine.simplemq.dao.AckMessage;
import cn.czfshine.simplemq.dao.Product;
import cn.czfshine.simplemq.dao.RequestMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 消息管道-与服务器交互，且会阻塞调用者。
 *
 * @author:czfshine
 * @date:2018/11/27 10:01
 */

public class BlockServerChannel {

    private Logger logger = LogManager.getLogger("channel");
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    /**
     * 初始化消息管道并链接消息队列服务
     *
     * @param url  服务端地址
     * @param port 服务端端口
     * @throws IOException 链接失败
     */
    public BlockServerChannel(String url, int port) throws IOException {
        socket = new Socket(url, port);
        socket.setSendBufferSize(4096);
        socket.setTcpNoDelay(true);
        socket.setSoTimeout(60 * 1000);
        socket.setKeepAlive(true);

    }

    private int id = 0;

    /**
     * 数据入队.
     * 阻塞方法，直到服务器回送成功标志才返回
     *
     * @param n 待入队的数据
     * @throws InterruptedException
     */
    public void put(long n) throws InterruptedException, IOException, ClassNotFoundException {
        logger.info("will put " + n);
        //打包数据
        Product product = new Product(id, n, 1);

        //发送数据包
        logger.info("will write::getoos");
        oos = oos == null ? new ObjectOutputStream(socket.getOutputStream()) : oos;
        logger.info("will write::writeobject");
        oos.writeObject(product);
        oos.flush();
        logger.info("Send OK");
        ois = ois == null ? new ObjectInputStream(socket.getInputStream()) : ois;
        logger.info("will read");

        //等待服务器确认
        AckMessage ack = null;
        while (ack == null) {
            logger.info("Has respone");
            ack = (AckMessage) ois.readObject();
            //todo:不确定要不要，应该在上一句阻塞的
            Thread.sleep(1);
        }
        logger.info("put ok");


    }

    /**
     * 数据出队.
     * 会阻塞到服务端返回数据包
     *
     * @return 队头数据
     * @throws InterruptedException
     * @see BlockServerChannel#put
     */
    public long take() throws InterruptedException, IOException, ClassNotFoundException {
        logger.info("will take ");
        logger.info("will read");
        oos = oos == null ? new ObjectOutputStream(socket.getOutputStream()) : oos;
        ois = ois == null ? new ObjectInputStream(socket.getInputStream()) : ois;
        logger.info("will write");

        //发送请求消息
        oos.writeObject(new RequestMessage(10));
        oos.flush();
        logger.info("Send OK");
        //等待接收
        Product p = null;
        while (p == null) {
            logger.info("Has respone");
            p = (Product) ois.readObject();
            Thread.sleep(1);
        }
        logger.info("take ok" + p);
        //解包数据
        return p.getData();


    }
}
