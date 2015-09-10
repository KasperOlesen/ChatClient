package Client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import Protocol.ProtocolStrings;
import javax.swing.text.html.HTML;

public class Client
{
  Socket socket;
  private int port;
  private InetAddress serverAddress;
  private static Scanner input;
  private PrintWriter output;
  
  public void connect(String address, int port) throws UnknownHostException, IOException
  {
    this.port = port;
    serverAddress = InetAddress.getByName(address);
    socket = new Socket(serverAddress, port);
    input = new Scanner(socket.getInputStream());
    output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
  }
  
  public void send(String msg)
  {
    output.println(msg);
  }
  
  public void stop() throws IOException{
    output.println(ProtocolStrings.STOP);
  }
  
  public String receive()
  {
    String msg = input.nextLine();
    if(msg.equals(ProtocolStrings.STOP)){
      try {
        socket.close();
      } catch (IOException ex) {
        Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
    return msg;
  }
  
  public static void main(String[] args)
  {   
    int port = 9090;
    String ip = "localhost";
    if(args.length == 2){
      port = Integer.parseInt(args[0]);
      ip = args[1];
    }
    try {
      Client myClient = new Client();      
      myClient.connect(ip, port);
      System.out.println("Connected to: \n" + 
                            "ip: " + ip + " port: " + port);
      
      myClient.send(input.nextLine());
      
      System.out.println(myClient.receive()); //Important Blocking call  
      
      myClient.stop();      
      //System.in.read();      
    } catch (UnknownHostException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    } catch (IOException ex) {
      Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
    }
  }
}
