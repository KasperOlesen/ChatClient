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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private final List<ClientHandler> clientHandlerList = new LinkedList();
    Map<String, ClientHandler> userMap;
    String username;
    String recievers;

    public static void stopServer() {
        keepRunning = false;
    }

    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Server started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler clientHandler = new ClientHandler(socket, this);

                userMap = new HashMap();

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

    public void messageHandler(String msg, String clientName) {
        if (msg.contains("#")) {
            String[] commandInput = msg.split("#");
            String command = commandInput[0].toUpperCase();
            recievers = commandInput[1];
            msg = clientName + ": " + commandInput[2];

            if (command.equalsIgnoreCase("MSG") && (userMap.containsKey(recievers))) {
                send(msg, userMap.get(recievers));
            }

        } else {
            send("Error: Incompatible input! Missing '#' \n"
                    + "Use MSG#Username#message to message others.", userMap.get(recievers));

        }
    }

    public boolean syntaxCheck(String msg) {
        String[] commandInput;
        String[] recieverInfo;
        boolean syntaxApproved;
        if (msg.startsWith("MSG#")) {
            commandInput = msg.split("#");
            String command = commandInput[0].toUpperCase();
            String reciever = commandInput[1];

            if (commandInput.length == 3) {
                if (reciever.contains(",")) {
                    recieverInfo = reciever.split(",");
                    syntaxApproved = recieverInfo.length > 1;
                } else {
                    syntaxApproved = true;
                }
            } else {
                syntaxApproved = false;
            }
        } else {
            syntaxApproved = false;
        }
        return syntaxApproved;
    }

    public void send(String msg, ClientHandler reciever) {

        reciever.send(msg);

    }
}
