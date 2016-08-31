package com.loggle.rpc.sea.file;

/**
 * @author guomy
 * @create 2016-08-31 11:05.
 */
public class StopWatch {
    private long startTime;
    private long endTime;

    private String taskName;


    public void startWithTaskName(String taskName) {
        this.taskName = taskName;
        System.out.println("start -----  " + taskName);
        this.startTime = System.currentTimeMillis();
    }

    public void stopAndPrint() {
        this.endTime = System.currentTimeMillis();
        System.out.println("end task ----- " + taskName + " ### waste time: " + (endTime - startTime) + " ms...");
    }
}
