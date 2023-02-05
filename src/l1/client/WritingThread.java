package l1.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;

import static l1.server.ChatServer.EXIT_WORD;

/**
 * This thread is responsible for reading input from the user and send it to the server.
 * It runs in an infinite loop until user leaves the chat.
 */
public class WritingThread extends Thread {
    private PrintWriter writer;
    private Socket socket;
    private ChatClient client;

    public WritingThread(Socket socket, ChatClient client) {
        this.socket = socket;
        this.client = client;

        try {
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
        } catch (IOException ex) {
            System.out.println("Error getting output stream: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public void run() {

        Scanner in;
        in = new Scanner(System.in);
        System.out.println("Enter your username:");
        String userName = in.nextLine();
        client.setUserName(userName);
        writer.println(userName);

        String text;

        do {
            System.out.println("[" + userName + "]: ");
            text = in.nextLine();
            writer.println(text);

        } while (!text.equals(EXIT_WORD));

        try {
            client.readingThread.executing = false;
            Thread.sleep(1000);
            socket.close();
        } catch (IOException | InterruptedException ex) {

            System.out.println("Error writing to server: " + ex.getMessage());
        }
    }
}