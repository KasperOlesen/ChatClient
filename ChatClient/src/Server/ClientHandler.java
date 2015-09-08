package Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import Protocol.ProtocolStrings;

/**
 * @author Kasper
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final PrintWriter writer;
    private final Scanner input;
    private final EchoServer server;

    public ClientHandler(Socket socket, EchoServer server) throws IOException{
        this.socket = socket;
        input = new Scanner(socket.getInputStream());
        writer = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
    }

    public void run() {
        try {
            String message = input.nextLine(); //IMPORTANT blocking call
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));
            while (!message.equals(ProtocolStrings.STOP)) {
                this.server.send(message.toUpperCase());
                Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));
                message = input.nextLine(); //IMPORTANT blocking call
            }
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            socket.close();
            Logger.getLogger(EchoServer.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void send(String msg){
        writer.println(msg);
    }
}
