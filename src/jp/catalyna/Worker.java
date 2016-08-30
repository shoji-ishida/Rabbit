package jp.catalyna;

import java.io.IOException;

/**
 * Created by ishida on 2016/08/30.
 */
public class Worker implements Runnable{
    private Send send;
    private int count;

    public Worker(int count) throws IOException {
        send = new Send();
        this.count = count;
    }

    @Override
    public void run() {
        for (int i = 1; i <= count; i++) {
            try {
                send.send(i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
