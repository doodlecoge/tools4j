package wang.huaichao;

import javafx.concurrent.Worker;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hch on 2015/2/12.
 */
public class ThreadPool {
    private int poolSize = 10;
    private List<Runnable> taskQueue = new ArrayList<Runnable>();
    private List<Worker> workers = new ArrayList<Worker>();

    public ThreadPool() {
        for (int i = 0; i < poolSize; i++) {
            Worker worker = new Worker(this);
            workers.add(worker);
            worker.start();
        }
    }

    public ThreadPool(int poolSize) {
        this.poolSize = poolSize;
    }

    public synchronized void addTask(Runnable task) {
        this.taskQueue.add(task);
        this.notifyAll();
    }

    public synchronized Runnable getTask()
            throws InterruptedException {
        while (this.taskQueue.size() == 0) {
            this.wait();
        }
        return this.taskQueue.remove(0);
    }

    public void join() throws InterruptedException {
        while (this.taskQueue.size() > 0) {
            Thread.sleep(100);
        }

        for (Worker worker : workers) {
            worker.halt();
            worker.join();
        }
    }

    private final class Worker extends Thread {
        private boolean stop = false;
        private ThreadPool pool;

        public Worker(ThreadPool pool) {
            this.pool = pool;
        }

        public void halt() {
            this.interrupt();
            this.stop = true;
        }

        @Override
        public void run() {
            while (!stop) {

                try {
                    Thread.sleep(10);
                    Runnable task = pool.getTask();
                    task.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }
}
