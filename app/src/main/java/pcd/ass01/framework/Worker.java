package pcd.ass01.framework;

import java.util.concurrent.BlockingQueue;

public class Worker implements Runnable {
    private BlockingQueue<WorkerTask> tasksQueue;

    public Worker(BlockingQueue<WorkerTask> tasksQueue) {
        this.tasksQueue = tasksQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WorkerTask task = tasksQueue.take();
                task.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}