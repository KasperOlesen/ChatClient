package Server;

import Client.Client;
import java.io.IOException;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kasper
 */
public class ClientConnection extends Observable implements Runnable {

    private final String ip;
    private final int port;
    private Client client;

    public ClientConnection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public void run() {

        try {
            final Client client = new Client();
            client.connect(ip, port);
            Thread receiverThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        while (true) {
                            String response = client.receive();

                            setChanged();
                            notifyObservers(response);
                        }
                    } catch (Exception e) {
                    }
                }
            });

            receiverThread.start();
            client.send("Hello World");

            receiverThread.join();

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void close() {
        if (this.client != null) {
            try {
                this.client.stop();
            } catch (IOException ex) {
                Logger.getLogger(ClientConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
