package pcd.ass01.framework;

public interface Master {
    void submitTask(WorkerTask task);

    void shutdown();
}
