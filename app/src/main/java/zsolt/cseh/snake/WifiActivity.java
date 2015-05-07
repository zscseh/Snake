package zsolt.cseh.snake;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.os.Bundle;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import connection.wifi.WifiDirectAcceptThread;
import connection.wifi.WifiDirectBroadcastReceiver;
import connection.wifi.WifiDirectConnectThread;

/**
 * The wifi menu activity
 * Allows to create a multiplayer game or connect to another via wifi direct
 */
public class WifiActivity extends Activity implements ConnectionInfoListener {

    private IntentFilter intentFilter;
    private WifiP2pManager manager;
    private Channel channel;
    private BroadcastReceiver receiver;
    private WifiP2pManager.PeerListListener peerListListener;
    private List peerList;
    private ListView listView;
    private SimpleArrayMap devices;
    private WifiP2pInfo info;
    private Button btnStartGame;

    private WifiDirectAcceptThread acceptThread;
    private WifiDirectConnectThread connectThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        listView = (ListView) findViewById(R.id.listWifiDevices);
        devices = new SimpleArrayMap<>();

        peerList = new ArrayList();
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(WifiActivity.this,
                android.R.layout.simple_list_item_1, peerList);
        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peers) {

                peerList.clear();
                devices.clear();

                for (WifiP2pDevice device : peers.getDeviceList()) {
                    devices.put(device.deviceAddress, device);
                    peerList.add(device.deviceAddress);
                }

                arrayAdapter.notifyDataSetChanged();
            }
        };

        receiver = new WifiDirectBroadcastReceiver(manager, channel, this, peerListListener);

        listView.setAdapter(arrayAdapter);

        Button btnStartDiscovery = (Button) findViewById(R.id.btnWifiStartDiscovery);
        btnStartDiscovery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e("wifi", "Cannot discover devices - reason: " + String.valueOf(reason));
                        Toast.makeText(WifiActivity.this, "Cannot discover devices.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnStartGame = (Button) findViewById(R.id.btnWifiStartGame);
        btnStartGame.setEnabled(false);
        btnStartGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectThread = new WifiDirectConnectThread(WifiActivity.this, info);
                connectThread.start();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView clickedView = (TextView) view;

                WifiP2pDevice clickedDevice = (WifiP2pDevice) devices.get(clickedView.getText());
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = clickedDevice.deviceAddress;

                manager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        if (acceptThread != null) {
                            acceptThread.requestStop();
                        }
                        acceptThread = new WifiDirectAcceptThread(WifiActivity.this);
                        acceptThread.start();
                        Toast.makeText(WifiActivity.this, "Starting discovery...", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.e("wifi", "Cannot connect to device - reason: " + String.valueOf(reason));
                        Toast.makeText(WifiActivity.this, "Cannot connect to this device.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void startGame() {
        Intent intent = new Intent(WifiActivity.this, MultiplayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo info) {
        this.info = info;
    }

    public void enableStartButton() {
        btnStartGame.setEnabled(true);
    }
}
