package l1.client;

import java.net.*;
import java.io.*;

/**
 * Starts the client.
 */
public class ChatClient {
    public ReadingThread readingThread;
    public WritingThread writingThread;
    private String hostName;
    private int port;
    private String userName;

    public ChatClient(String hostname, int port) {
        this.hostName = hostname;
        this.port = port;
    }

    public void execute() {
        try {
            System.out.println("Try new connection. hostname: " + hostName + " port: " + port);
            Socket socket = new Socket(hostName, port);

            System.out.println("Connected to the chat");

            readingThread = new ReadingThread(socket, this);
            readingThread.start();
            writingThread = new WritingThread(socket, this);
            writingThread.start();

        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O Error: " + ex.getMessage());
        }

    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    String getUserName() {
        return this.userName;
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Arguments error");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        ChatClient client = new ChatClient(hostname, port);
        client.execute();
    }
}