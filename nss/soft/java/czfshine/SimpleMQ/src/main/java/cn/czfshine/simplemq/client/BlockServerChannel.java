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
 * @author:czfshine
 * @date:2018/11/27 10:01
 */

public class BlockServerChannel {

    Logger logger= LogManager.getLogger("channel");
    ObjectOutputStream oos;
    ObjectInputStream ois;
    private Socket socket;
    public BlockServerChannel(String url,int port){
        try {
            socket=new Socket(url,port);
            socket.setSendBufferSize(4096);
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(60*1000);
            socket.setKeepAlive(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int id=0;
    void put(long n) throws InterruptedException
    {
        logger.info("will put "+n);
        Product product = new Product(id, n, 1);

        try {
            logger.info("will write");
            oos= oos==null?new ObjectOutputStream(socket.getOutputStream()):oos;
            logger.info("will write");

            oos.writeObject(product);
            oos.flush();
            ois = ois==null?new ObjectInputStream(socket.getInputStream()):ois;
            logger.info("will write");
            logger.info("Send OK");
            AckMessage ack = null;
            while(ack==null){
                    logger.info("Has respone");
                    ack = (AckMessage) ois.readObject();
                Thread.sleep(1);
            }
            logger.info("put ok");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
    long take() throws InterruptedException{
        logger.info("will take ");

        try {
            logger.info("will read");
            oos= oos==null?new ObjectOutputStream(socket.getOutputStream()):oos;
            ois = ois==null?new ObjectInputStream(socket.getInputStream()):ois;
            logger.info("will write");

            oos.writeObject(new RequestMessage(10));
            oos.flush();

            logger.info("Send OK");
            Product p = null;
            while(p==null){
                logger.info("Has respone");
                p = (Product) ois.readObject();
                Thread.sleep(1);
            }
            logger.info("take ok"+p);
            return p.getData();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
