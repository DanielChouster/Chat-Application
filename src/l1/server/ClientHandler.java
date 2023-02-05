package l1.server;

import java.io.*;
import java.net.*;

import static l1.server.ChatServer.EXIT_WORD;

/**
 * This thread provides connection for each connected client
 */
public class ClientHandler extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);

            printUsers();

            String userName = reader.readLine();
            server.addUserName(userName);
            System.out.println(userName);

            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);

            String clientMessage;

            do {
                clientMessage = reader.readLine();
                serverMessage = "[" + userName + "]: " + clientMessage;
                server.broadcast(serverMessage, this);
                if (clientMessage == null)
                {
                    break;
                }

            } while (!clientMessage.equals(EXIT_WORD));

            server.removeUser(userName, this);
            socket.close();

            serverMessage = userName + " has left.";
            server.broadcast(serverMessage, this);

        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Sends a list of connected users to the new user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Connected users: " + server.getUserNames());
        } else {
            writer.println("No other users connected");
        }
    }

    /**
     * Message to client.
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}