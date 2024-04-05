package pcd.ass01.framework;

import java.util.LinkedList;
import java.util.Queue;

public class ThreadSafeQueue<T> {
    private Queue<T> queue;
    private int capacity;

    public ThreadSafeQueue() {
        this.queue = new LinkedList<>();
        this.capacity = Integer.MAX_VALUE;
    }

    public synchronized void enqueue(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }

    public synchronized T dequeue() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.remove();
        notifyAll();
        return item;
    }

    public synchronized int size() {
        return queue.size();
    }
}
