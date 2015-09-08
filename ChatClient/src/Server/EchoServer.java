package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Utils;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private final List<ClientHandler> clientHandlerList = new LinkedList();

    public static void stopServer() {
        keepRunning = false;
    }
    
    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Sever started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlerList.add(clientHandler);
                clientHandler.start();
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(EchoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new EchoServer().runServer();

        try {
            String logFile = properties.getProperty("logFile");
            Utils.setLogFile(logFile, EchoServer.class.getName());
        } finally {
            Utils.closeLogger(EchoServer.class.getName());
        }
    }
    
    
    public void send(String msg){
        for (ClientHandler clientHandler : clientHandlerList){
            clientHandler.send(msg);
        }
    }
}

