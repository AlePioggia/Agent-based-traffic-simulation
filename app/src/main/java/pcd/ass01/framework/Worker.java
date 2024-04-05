package pcd.ass01.framework;

public class Worker implements Runnable {
    private ThreadSafeQueue<WorkerTask> tasksQueue;

    public Worker(ThreadSafeQueue<WorkerTask> tasksQueue) {
        this.tasksQueue = tasksQueue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WorkerTask task = tasksQueue.dequeue();
                task.execute();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
