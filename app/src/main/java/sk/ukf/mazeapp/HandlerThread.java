package sk.ukf.mazeapp;

import android.os.Handler;

public class HandlerThread extends Thread {
    Handler cleaningHandler;
    boolean interrupt;

    public HandlerThread(Handler handler) {
        super();
        cleaningHandler = handler;
        interrupt = true;
    }

    public void setInterrupt() {
        interrupt = false;
    }

    @Override
    public void run() {
        while (interrupt) {
            try {
                this.sleep(60);
            } catch (Exception ex) {
            }
            cleaningHandler.sendEmptyMessage(0);
        }
    }
}

