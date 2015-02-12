package wang.huaichao.net;

import wang.huaichao.ThreadPool;

/**
 * Created by hch on 2015/2/12.
 */
public class ThreadPoolTest {
    private static class Too implements Runnable {
        public int id;

        @Override
        public void run() {
            System.out.println(id);
        }
    }
    public static void main(String[] args) throws InterruptedException {
        ThreadPool tp = new ThreadPool();
        for (int i = 0; i < 100; i++) {
            Too too = new Too();
            too.id = i;
            tp.addTask(too);
        }
        tp.join();
    }
}
