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
            double random = Math.random();
            int s = (int) (random * 1000 * 2);
            try {
                Thread.sleep(s);
            } catch (InterruptedException e) {
                System.out.println("==================== break");
            }
            System.out.println(id);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ThreadPool tp = new ThreadPool();
        for (int i = 0; i < 20; i++) {
            Too too = new Too();
            too.id = i;
            tp.addTask(too);
        }
        tp.join();
    }
}
