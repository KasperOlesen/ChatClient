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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EchoServer {

    private static boolean keepRunning = true;
    private static ServerSocket serverSocket;
    private static final Properties properties = Utils.initProperties("server.properties");
    private final List<ClientHandler> clientHandlerList = new LinkedList();
    Map<String, ClientHandler> userMap = new HashMap();
    String username;
    String reciever;

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

        System.out.println(msg);

        if (msg.startsWith("MSG") && msg.contains("#")) {
            String[] commandInput = msg.split("#");
            reciever = commandInput[1];
            msg = clientName + ": " + commandInput[2];
            
            if (reciever.contains(",") && reciever.length() > 1) {

                String[] recieverList = reciever.split(",");
                ArrayList<String> aList = new ArrayList<>(userMap.keySet());
                for (String s : aList) {
                    for (String recieverList1 : recieverList) {
                        if (s.equalsIgnoreCase(recieverList1)) {
                            
                            send(msg, userMap.get(recieverList1.toUpperCase()));
                        }
                    }
                }
            }
            else if (userMap.containsKey(reciever)) {
                send(msg, userMap.get(reciever));
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

        if (msg.startsWith("MSG") && msg.contains("#")) {
            commandInput = msg.split("#");
            String command = commandInput[0];
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

    public void sendUserlistToAll() {
        ArrayList<String> userList = new ArrayList<>(userMap.keySet());
        for (String user : userList) {
            send("USERLIST# " +userList.toString(), userMap.get(user));
            
        }
    }

    public void send(String msg, ClientHandler reciever) {

        reciever.send(msg);

    }
}
