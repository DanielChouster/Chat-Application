package l1.client;

import java.io.*;
import java.net.*;

import static l1.server.ChatServer.EXIT_WORD;

/**
 * This thread reads server input and prints it to the console.
 * There is an infinite loop until client disconnects from server.
 */
public class ReadingThread extends Thread {
    public boolean executing = true;
    private BufferedReader reader;
    private Socket socket;
    private ChatClient client;

    public ReadingThread(Socket socket, ChatClient client) {
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

    // Infinite loop until client wants to leave
    public void run() {
        while (executing) {
            try {
                String response = reader.readLine();
                if (response == null)
                    continue;
                if (response.equals(EXIT_WORD)) {
                    System.out.println("Exit from server");
                    socket.close();
                    System.exit(0);
                    break;
                }
                System.out.println("\n" + response);

                // Prints the username of the client
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