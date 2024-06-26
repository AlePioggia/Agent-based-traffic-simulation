package pcd.ass01.framework;

import java.util.ArrayList;
import java.util.List;

public class MasterImpl implements Master {

    private ThreadSafeQueue<WorkerTask> taskQueue;
    private List<Thread> workers;

    public MasterImpl() {
        this.taskQueue = new ThreadSafeQueue<>();
        this.workers = new ArrayList<Thread>();

        for (int i = 0; i < SetupConstants.NUM_WORKERS.getValue(); i++) {
            Worker worker = new Worker(taskQueue);
            Thread workerThread = new Thread(worker);
            workers.add(workerThread);
            workerThread.start();
        }
    }

    public MasterImpl(int numWorkers) {
        this.taskQueue = new ThreadSafeQueue<>();
        this.workers = new ArrayList<Thread>();

        for (int i = 0; i < numWorkers; i++) {
            Worker worker = new Worker(taskQueue);
            Thread workerThread = new Thread(worker);
            workers.add(workerThread);
            workerThread.start();
        }
    }

    @Override
    public void submitTask(WorkerTask task) {
        try {
            taskQueue.enqueue(task);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void shutdown() {
        for (Thread worker : workers) {
            worker.interrupt();
        }

        for (Thread worker : workers) {
            try {
                worker.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
