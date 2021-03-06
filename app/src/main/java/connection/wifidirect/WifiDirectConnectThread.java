package connection.wifidirect;

import android.net.wifi.p2p.WifiP2pInfo;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import connection.ConnectionProperties;
import connection.enumeration.DeviceType;
import zsolt.cseh.snake.WifiDirectActivity;

/**
 * Created by Zsolt on 2015.05.02..
 * <p/>
 * Connects to another device, which run a WifiDirectAcceptThread
 */
public class WifiDirectConnectThread extends Thread {

    private WifiDirectActivity activity;
    private WifiP2pInfo info;

    public WifiDirectConnectThread(WifiDirectActivity activity, WifiP2pInfo info) {
        this.activity = activity;
        this.info = info;
        ConnectionProperties.getInstance().setDeviceType(DeviceType.CLIENT);
    }

    /**
     * Connects another device via the socket
     */
    @Override
    public void run() {
        Socket socket = new Socket();
        String host;

        try {
            host = info.groupOwnerAddress.getHostAddress();
        } catch (NullPointerException e) {
            e.printStackTrace();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, "An error occurred, please try to connect again", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        try {
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, 8888)), 5000);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ConnectionProperties.getInstance().setSocket(new WifiDirectSocket(socket));
        activity.startGame();
    }
}
