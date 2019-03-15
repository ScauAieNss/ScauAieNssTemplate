package cn.czfshine.simplemq.client;

import java.io.IOException;

/**消费者客户端.
 * 从管道取一个整数，判断是否为素数（消费数据）
 * @author:czfshine
 * @date:2018/11/27 9:46
 */

public class ComsumerClient {

    private static long mul(long a,long b,long n)
    {
        long ans=0;
        while(b!=0)
        {

            if((b&1)!=0) ans=(ans+a)%n;
            a=(2*a)%n;
            b=b>>>1;
        }
        return ans;
    }
    private static long Pow(long a,long b,long n)
    {
        long result=1;
        long base=a%n;
        while(b!=0)
        {

            if((b&1)!=0) result = mul(result, base, n);
            base=mul(base,base,n);
            b=b>>>1;
        }
        return result;
    }


    /**判断是否（可能）为素数.
     * @param n 待判断的数字
     * @return false一定不是，true可能是
     */
    private boolean isPa(long n){
        long[] pan = {2, 3, 5, 7,11,31,61,73,233,331};
        int i;
        for(i=0; i<10; i++)
            if(Pow(pan[i],n-1,n)!=1)break;//if(pan[i]^(n-1)%n==1)this int is prime

        if(i==10)
            return true;
        else
            return false;

    }


    private BlockServerChannel channel; //消息管道
    public void start() throws InterruptedException {
        try {
            //服务器地址和端口看服务端的配置
            channel=new BlockServerChannel("127.0.0.1",6666);
            //对应的生产者至少要生产10000个数据，并送给消息服务，
            // 不然消费者会一直等待的
            for(int i=0;i<10000;i++){
                long res=channel.take();
                isPa(res);
                //nothing here
            }
        } catch (IOException | ClassNotFoundException e) {
            //异常根据实际情况处理
            e.printStackTrace();
        }


    }

    public static void main(String []arg) throws InterruptedException {
        new ComsumerClient().start();
    }
}
