package test;

import android.util.Log;

import connection.TransferThread;
import control.TimeManager;
import zsolt.cseh.snake.TestActivity;

/**
 * Created by zscse on 2015. 10. 05..
 */
public class SenderThread extends Thread {

    private TransferThread thread;
    private TestActivity activity;

    public SenderThread(TransferThread thread, TestActivity activity) {
        this.thread = thread;
        this.activity = activity;
    }

    @Override
    public void run() {
        TestPacket.resetId();
//        for (int i = 0; i < 500; i++) {
        for (int i = 0; i < 20; i++) {
            TestPacket packet = activity.createPacket(((i / 10) + 1) * 2);
//            TestPacket packet = activity.createPacket(200 + (i / 10) + 1);
            thread.write(packet);
            Log.i("packet", "sent;" + packet.getSender().toString() + ";" + packet.getId() + ";" +
                    TimeManager.getTime(packet.getTimestamp()) + ";" + packet.getLength());
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Log.v("error", e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
