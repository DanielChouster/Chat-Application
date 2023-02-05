package net.codejava.networking.chat.client;

import java.io.*;
import java.net.*;

/**
 * This thread is responsible for reading server's input and printing it
 * to the console.
 * It runs in an infinite loop until the client disconnects from the server.
 *
 * @author www.codejava.net
 */
public class ReadThread extends Thread {
public boolean execute=true;
private BufferedReader reader;
private Socket socket;
private ChatClient client;

public ReadThread(Socket socket, ChatClient client) {
this.socket = socket;
this.client = client;

try {
InputStream input = socket.getInputStream();
reader = new BufferedReader(new InputStreamReader(input));
} catch (IOException ex) {
System.out.println("Error getting input stream: " + ex.getMessage());
ex.printStackTrace();
}
}

public void run() {
while (execute) {
try {
String response = reader.readLine();
if (response==null)
    continue;
if (response.equals("quit"))
    {
    System.out.println("Receive exit from server");
    socket.close();
    System.exit(0);
    break;
    }
System.out.println("\n" + response);

// prints the username after displaying the server's message
if (client.getUserName() != null) {
System.out.print("[" + client.getUserName() + "]: ");
}
} catch (IOException ex) {
System.out.println("Error reading from server: " + ex.getMessage());
ex.printStackTrace();
break;
}
}
}
}