package zsolt.cseh.snake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import connection.ConnectionManager;
import connection.ConnectionSocket;
import connection.SnakePacket;
import connection.TransferThread;
import connection.enumeration.DeviceType;
import control.TimeManager;
import model.enumeration.Direction;
import test.SenderThread;
import test.TestManager;
import test.TestPacket;
import test.TestTimingThread;

public class TestActivity extends Activity {

    private Random random;
    private TestManager testManager;
    private TransferThread transferThread;
    private TestTimingThread testTimingThread;
    private List<String> packets;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        random = new Random();

        ConnectionSocket socket = ConnectionManager.getInstance().getSocket();
        transferThread = new TransferThread(socket);
        transferThread.start();

        testTimingThread = new TestTimingThread(this);
        testTimingThread.start();

        Button btnSend = (Button) findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.v("send", "send");
//                transferThread.write(createPacket());
                SenderThread thread = new SenderThread(transferThread, TestActivity.this);
                thread.start();
            }
        });

//        packets = new ArrayList<>();
//        arrayAdapter = new ArrayAdapter<>(TestActivity.this, android.R.layout.simple_list_item_1, packets);
//
//        ListView testList = (ListView) findViewById(R.id.testList);
//        testList.setAdapter(arrayAdapter);
    }

    public TestPacket createPacket() {

        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < random.nextInt(1000); i++) {
            list.add(random.nextInt(100));
        }

        long timestamp = TimeManager.getTime();

//        packets.add(TimeManager.getTime(timestamp + ConnectionManager.getInstance().getOffset()) + " - " + list.size());
        long offset = ConnectionManager.getInstance().getOffset();
        String string = TimeManager.getTime(timestamp + offset);

//        Log.v("createPacket", string + " - " + String.valueOf(list.size()));
//        arrayAdapter.notifyDataSetChanged();

        TestPacket packet = new TestPacket(timestamp + ConnectionManager.getInstance().getOffset(), list);
//        Log.v("packetOffset", String.valueOf(ConnectionManager.getInstance().getOffset()));
//        Log.v("packet", packet.getSender().toString() + ";" + packet.getId() + ";" +
//                TimeManager.getTime(packet.getTimestamp()) + ";" + packet.getLength());

        return packet;
    }

    public SnakePacket createSnakePacket() {
        SnakePacket packet = new SnakePacket(Direction.DOWN, 10, 10);
        Log.v("packet", "send" + String.valueOf(TimeManager.getTime(TimeManager.getTime() + ConnectionManager.getInstance().getOffset())));
        return packet;
    }

    public void receivePacket() {
        TestPacket packet = (TestPacket) transferThread.getPacket();

        if (packet != null) {
//            addPacket(packet.getTimestamp(), packet.getLength());
            Log.v("packet", "received;" + packet.getSender().toString() + ";" + packet.getId() + ";" +
                    TimeManager.getTime(TimeManager.getTime() - ConnectionManager.getInstance().getOffset()) + ";" + packet.getLength());
        }
    }

    public void receiveSnakePacket() {
        SnakePacket packet = (SnakePacket) transferThread.getPacket();
        if(packet != null) {
            Log.v("packet", "received " + String.valueOf(TimeManager.getTime(TimeManager.getTime() + ConnectionManager.getInstance().getOffset())));
        }
    }

    private void addPacket(final long timestamp, final int length) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                packets.add(TimeManager.getTime(TimeManager.getTime() + ConnectionManager.getInstance().getOffset()) + " - " + length);
                arrayAdapter.notifyDataSetChanged();
                Log.v("receive", "receive");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        testTimingThread.requestStop();
    }
}
