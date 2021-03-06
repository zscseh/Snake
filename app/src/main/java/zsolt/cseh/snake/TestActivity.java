package zsolt.cseh.snake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import connection.ConnectionProperties;
import connection.TransferThread;
import model.TimeManager;
import test.SenderThread;
import test.TestPacket;
import test.TestTimingThread;

/**
 * The test activity
 * Allows to measure connections
 */
public class TestActivity extends Activity {

    private TransferThread transferThread;
    private TestTimingThread testTimingThread;
    private List<TestPacket> packets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        transferThread = ConnectionProperties.getInstance().getTransferThread();

        testTimingThread = new TestTimingThread(this);
        testTimingThread.start();

        packets = new ArrayList<>();

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SenderThread thread = new SenderThread(transferThread, TestActivity.this);
                thread.start();
            }
        });

        Button btnLog = (Button) findViewById(R.id.btnLog);
        btnLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<TestPacket> logPackets = new ArrayList<>();
                for (int i = 0; i < packets.get(packets.size() - 1).getId(); i++) {
                    logPackets.add(new TestPacket(0, new ArrayList<Integer>()));
                }

                for (TestPacket packet : packets) {
                    logPackets.set(packet.getId() - 1, packet);
                }

                Log.i("packet", String.valueOf(logPackets.size()));
                for (TestPacket packet : logPackets) {
                    if (packet.getLength() != 0) {
                        Log.i("packet", getLog(packet));
                    } else {
                        Log.i("packet", " ");
                    }
                }
            }
        });
    }

    public TestPacket createPacket(int length) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            list.add(50);
        }

        return new TestPacket(TimeManager.getTime() + ConnectionProperties.getInstance().getOffset(), list);
    }

    public void receivePacket() {
        try {
            TestPacket packet = (TestPacket) transferThread.getPacket();

            if (packet != null) {
                packet.setTimestamp(TimeManager.getTime());
                packets.add(packet);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private String getLog(TestPacket packet) {
        return "received;" + packet.getSender().toString() + ";" + packet.getId() + ";" +
                TimeManager.getTime(packet.getTimestamp() + ConnectionProperties.getInstance().getOffset()) +
                ";" + packet.getLength();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        transferThread.cancel();
        testTimingThread.requestStop();
    }
}
