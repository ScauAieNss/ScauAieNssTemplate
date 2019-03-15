package cn.czfshine.simplemq.client;

/**
 * @author:czfshine
 * @date:2018/11/27 9:46
 */

public class ComsumerClient {

    static long mul(long a,long b,long n)
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
    static long Pow(long a,long b,long n)
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



    private boolean isPa(long n){
        long s=System.currentTimeMillis();
        long[] pan = {2, 3, 5, 7,11,31,61,73,233,331};
        int i;
        for(i=0; i<10; i++)
            if(Pow(pan[i],n-1,n)!=1)break;//if(pan[i]^(n-1)%n==1)this int is prime

        if(i==10)
            return true;
        else
            return false;

    }


    BlockServerChannel channel;
    public void start() throws InterruptedException {
        channel=new BlockServerChannel("127.0.0.1",6666);

        for(int i=0;i<10000;i++){
            long res=channel.take();
            isPa(res);
        }

    }

    public static void main(String []arg) throws InterruptedException {
        new ComsumerClient().start();
    }
}
