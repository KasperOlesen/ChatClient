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
    private PrintWriter writer;
    private Scanner input;
    private final TcpServer server;
    private boolean nameChanged = false;
    private String chosenName;

    public ClientHandler(Socket socket, TcpServer server) throws IOException {
        this.socket = socket;
        this.server = server;
    }

    public void run() {

        try {

            input = new Scanner(socket.getInputStream());
            writer = new PrintWriter(socket.getOutputStream(), true);

            while (!nameChanged) {
                writer.println("Please select a username By typing USER#YourUsernameHere");

                String temp = input.nextLine();
                System.out.println("temp = " + temp);
                boolean UserCheck = server.checkUserName(temp);
                if (UserCheck) {
                    chosenName = server.setClientName(temp, this);
                    nameChanged = true;
                    server.sendUserlistToAll();
                }

            }

            String message = input.nextLine(); //IMPORTANT blocking call
            Logger.getLogger(TcpServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message));

            while (!message.equals(ProtocolStrings.STOP)) {

                if (this.server.syntaxCheck(message)) {

                    this.server.messageHandler(message, chosenName);

                } else {
                    writer.println("SyntaxError: You have used a wrong command \n"
                            + "The correct command is: MSG#RECIEVER#MESSAGE");
                }
                Logger.getLogger(TcpServer.class.getName()).log(Level.INFO, String.format("Received the message: %1$S ", message.toUpperCase()));

                message = input.nextLine(); //IMPORTANT blocking call
            }

            server.userMap.remove(chosenName);
            server.sendUserlistToAll();
            writer.println(ProtocolStrings.STOP);//Echo the stop message back to the client for a nice closedown
            socket.close();
            Logger.getLogger(TcpServer.class.getName()).log(Level.INFO, "Closed a Connection");
        } catch (java.util.NoSuchElementException ex) {
            server.userMap.remove(chosenName);
            server.sendUserlistToAll();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void send(String msg) {
        writer.println(msg);
        writer.flush();

    }

}
