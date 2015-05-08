package connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Zsolt on 2015.05.02..
 * <p/>
 * Transfers packages between devices
 */
public class TransferThread extends Thread {

    private final ConnectionSocket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private Packet packet;
    private int arrivedPackets;
    private boolean stopSignal;

    public TransferThread(ConnectionSocket socket) {
        this.socket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;

        arrivedPackets = 0;

        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputStream = tmpIn;
        outputStream = tmpOut;
        stopSignal = false;
    }

    /**
     * Accepts packages from other devices
     */
    @Override
    public void run() {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        while (!stopSignal) {
            if (bytes != 0) {
                try {
                    packet = PacketSerialization.deserialize(buffer);
                    arrivedPackets++;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

            try {
                bytes = inputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Send packages to another device
     */
    public void write(Packet packet) {
        byte[] bytes = new byte[0];

        try {
            bytes = PacketSerialization.serialize(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns with the last accepted package
     */
    public Packet getPacket() {
        if (0 < arrivedPackets) {
            arrivedPackets--;
            return packet;
        } else {
            return null;
        }
    }

    public void cancel() {
        stopSignal = true;

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
