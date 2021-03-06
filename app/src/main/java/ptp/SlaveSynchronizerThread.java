package ptp;

import android.util.Log;

import connection.ConnectionProperties;
import connection.TransferThread;
import model.TimeManager;

/**
 * Created by zscse on 2015. 09. 29..
 *
 * Sends and receives PTP messages if the device is slave
 */
public class SlaveSynchronizerThread extends Thread {

    @Override
    public void run() {
        // Preparing for synchronization
        TransferThread transferThread = ConnectionProperties.getInstance().getTransferThread();

        int averageDelay = 0;

        for (int i = 0; i < 11; i++) {
            // Waiting for sync
            boolean wait_for_sync = true;
            SynchronizerPacket sync = new SynchronizerPacket(0);
            long sync_time = 0;

            while (wait_for_sync) {
                sync = (SynchronizerPacket) transferThread.getPacket();
                if (sync != null) {
                    sync_time = TimeManager.getTime();
                    wait_for_sync = false;
                }
            }

            // Sending delay_req
            long delay_req_time = TimeManager.getTime();
            transferThread.write(new SynchronizerPacket(0));

            // Waiting for delay_resp
            boolean wait_for_delay_resp = true;
            SynchronizerPacket delay_resp;
            long delay_resp_time = 0;

            while (wait_for_delay_resp) {
                delay_resp = (SynchronizerPacket) transferThread.getPacket();
                if (delay_resp != null) {
                    delay_resp_time = TimeManager.getTime();
                    wait_for_delay_resp = false;
                }
            }

            int delay = (int) (sync.getTime() - sync_time);
            int transit = (int) ((delay_resp_time - delay_req_time) / 2);

            if (i != 0) {
                averageDelay += delay + transit;
            }
        }

        averageDelay /= 10;
        ConnectionProperties.getInstance().setOffset(averageDelay);
        Log.i("sync", "average offset: " + ConnectionProperties.getInstance().getOffset());

        boolean wait_for_start = true;
        SynchronizerPacket startTimePacket;
        long startTime = 0;
        while(wait_for_start) {
            startTimePacket = (SynchronizerPacket) transferThread.getPacket();
            if(startTimePacket != null) {
                startTime = startTimePacket.getTime();
                wait_for_start = false;
            }
        }

        while (TimeManager.getTime() + ConnectionProperties.getInstance().getOffset() < startTime);
    }
}
