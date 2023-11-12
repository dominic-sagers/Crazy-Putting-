package main;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * This class is a queue for frames.
 */
public class FramesQueue {
    //The queue in which are they stored
    public static BlockingQueue<double[]> queue = new ArrayBlockingQueue<double[]>(100000000);
    public static boolean isDone = false;
    public static double[] lastFrame = new double[4];

    /**
     * @return the size of the queue
     */
    public static int size() {
        return queue.size();
    }

    /**
     * @return true if there are no elements, false if there are any
     */
    public static boolean isEmpty() {
        return queue.size()==0;
    }

    /**
     * adds frame to queue
     * @param frame to be added
     * @param isAdd should it be added
     */
    public static synchronized void add(double[] frame, boolean isAdd) {
        queue.add(frame);
    }

    /**
     * returns the top frame or if the calculation is in proccess and there are less than 2 frames then it only shows the top frame
     * without removing it from the queue
     * @return top frame
     */
    public static double[] poll(){
        if(queue.size()<2&&!isDone){
            return queue.peek();
        }

        return queue.poll();
    }

    /**
     * clears the queue
     */
    public static void clear() {
        queue.clear();
        isDone = false;
    }

    public static double[] getLastOne() {
        while(queue.size()>1){
            queue.poll();
        }
        return queue.poll();
    }
}

