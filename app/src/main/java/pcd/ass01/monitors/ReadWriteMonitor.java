package pcd.ass01.monitors;

public interface ReadWriteMonitor {

    void requestRead();

    void releaseRead();

    void requestWrite();

    void releaseWrite();
}
