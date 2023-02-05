package l1.server;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.exit;

/**
 * This class represents the chat server.
 */
public class ChatServer {
    public static String EXIT_WORD = "quit";
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<ClientHandler> userThreads = new HashSet<>();

    public ChatServer(int port) {
        this.port = port;
    }

    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Chat server listening on port " + port);
            System.out.println("Type '" + EXIT_WORD + "' to exit");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.print("New user entered the chat: ");

                ClientHandler newUser = new ClientHandler(socket, this);
                userThreads.add(newUser);
                newUser.start();

            }

        } catch (IOException ex) {
            System.out.println("Error in the server: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    static class ReadCommand implements Runnable {
        private final ChatServer server;

        public ReadCommand(ChatServer server) {
            this.server = server;
        }

        public void run() {
            Scanner in;
            while (true) {
                in = new Scanner(System.in);
                String input = in.nextLine();
                if (input.equals(EXIT_WORD)) {
                    //Exit
                    server.broadcast(EXIT_WORD, null);
                    exit(0);
                }
            }
        }
    }

    public static void main(String[] args) {

        if (args.length < 1) {
            System.out.println("Syntax: java ChatServer <port-number>");
            exit(0);
        }

        int port = Integer.parseInt(args[0]);

        ChatServer server = new ChatServer(port);
        ReadCommand readCommand = new ReadCommand(server);
        new Thread(readCommand).start();

        server.execute();

    }

    /**
     * Broadcasts a message from one user to another
     */
    void broadcast(String message, ClientHandler excludeUser) {
        for (ClientHandler aUser : userThreads) {
            if (aUser != excludeUser) {
                aUser.sendMessage(message);
            }
        }
    }

    /**
     * Stores username of the newly connected client.
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }

    /**
     * Remove the associated username and UserThread when a client leaves the chat
     */
    void removeUser(String userName, ClientHandler aUser) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(aUser);
            System.out.println("The user " + userName + " has left the chat.");
        }
    }

    Set<String> getUserNames() {
        return this.userNames;
    }

    /**
     * Returns true if there are other users connected (not counting the currently connected user)
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}