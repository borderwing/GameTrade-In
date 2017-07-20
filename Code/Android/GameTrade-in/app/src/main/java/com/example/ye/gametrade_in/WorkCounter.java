package com.example.ye.gametrade_in;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by ye on 2017/7/19.
 */

public class WorkCounter {
    private int runningTasks;
    //private final Context ctx;

    public void setRunningTasks(int num){
        runningTasks = num;
    }

    public void runningTasksRelease(){
        runningTasks --;
    }

    public int getRunningTasks(){
        return runningTasks;
    }

    /*public WorkCounter(int numberOfTasks, Context ctx) {
        this.runningTasks = numberOfTasks;
        this.ctx = ctx;
    }
    // Only call this in onPostExecute! (or add synchronized to method declaration)
    public void taskFinished() {
        if (--runningTasks == 0) {
            LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(this.ctx);
            mgr.sendBroadcast(new Intent("all_tasks_have_finished"));
        }
    }*/
}
