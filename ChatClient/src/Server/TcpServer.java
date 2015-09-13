package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import Utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TcpServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    Map<String, ClientHandler> userMap = new HashMap();
    String username;
    String reciever;

    public static void stopServer() {
        keepRunning = false;
    }

    private void runServer() {
        int port = Integer.parseInt(properties.getProperty("port"));
        String ip = properties.getProperty("serverIp");

        Logger.getLogger(TcpServer.class.getName()).log(Level.INFO, "Server started. Listening on: " + port + ", bound to: " + ip);
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(ip, port));
            do {
                Socket socket = serverSocket.accept(); //Important Blocking call
                Logger.getLogger(TcpServer.class.getName()).log(Level.INFO, "Connected to a client");
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandler.start();
            } while (keepRunning);
        } catch (IOException ex) {
            Logger.getLogger(TcpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        new TcpServer().runServer();

        try {
            String logFile = properties.getProperty("logFile");
            Utils.setLogFile(logFile, TcpServer.class.getName());
        } finally {
            Utils.closeLogger(TcpServer.class.getName());
        }
    }

    public void messageHandler(String msg, String clientName) {

        System.out.println(msg);

        if (msg.startsWith("MSG") && msg.contains("#")) {
            String[] commandInput = msg.split("#");
            reciever = commandInput[1];
            msg = clientName + ": " + commandInput[2];

            if (reciever.contains(",") && reciever.length() > 1) {

                String[] users = reciever.split(",");
                ArrayList<String> userMapKeyList = new ArrayList<>(userMap.keySet());
                for (String userKeyFromMap : userMapKeyList) {

                    for (String user : users) {

                        if (userKeyFromMap.equalsIgnoreCase(user)) {
                            send(msg, userMap.get(user.toUpperCase()));
                        }
                    }
                }
            } else if (userMap.containsKey(reciever.toUpperCase())) {
                send(msg, userMap.get(reciever.toUpperCase()));
            }
        } else {
            send("Error: Incompatible input! Missing '#' \n"
                    + "Use MSG#Username#message to message others.", userMap.get(reciever));
        }
    }

    public boolean syntaxCheck(String msg) {
        String[] commandInput;
        String[] recieverInfo;
        boolean syntaxApproved;

        if (msg.startsWith("MSG") && msg.contains("#") && msg.length() > 4) {

            commandInput = msg.split("#");
            String toClients = commandInput[1];

            if (commandInput.length == 3) {
                if (toClients.contains(",")) {
                    recieverInfo = toClients.split(",");
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

    public void sendUserlistToAll() {
        ArrayList<String> userList = new ArrayList<>(userMap.keySet());
        for (String user : userList) {
            send("USERLIST# " + userList.toString(), userMap.get(user));

        }
    }

    public void send(String msg, ClientHandler reciever) {
        reciever.send(msg);

    }

    public boolean checkUserName(String temp) {
        if (temp.startsWith("USER#")) {
            String[] nameInput = temp.split("#");
            if (nameInput.length == 2) {
                return true;
            }

        }
        return false;
    }

    public String setClientName(String temp, ClientHandler aThis) {
        String[] nameInput = temp.split("#");
        String chosenName = nameInput[1].toUpperCase();
        System.out.println(chosenName);
        userMap.put(chosenName, aThis);
        return chosenName;

    }

}
