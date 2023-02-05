package net.codejava.networking.chat.server;

import java.io.*;
import java.net.*;
import java.util.*;

import static java.lang.System.exit;

/**
 * This is the chat server program.
 * Press Ctrl + C to terminate the program.
 *
 * @author www.codejava.net
 */
public class ChatServer {
private int port;
private Set<String> userNames = new HashSet<>();
private Set<UserThread> userThreads = new HashSet<>();

public ChatServer(int port) {
this.port = port;
}

public void execute() {
try (ServerSocket serverSocket = new ServerSocket(port)) {

System.out.println("Chat Server is listening on port " + port);
System.out.println("Print 'quit' for exit");

while (true) {
Socket socket = serverSocket.accept();
System.out.print("New user connected");

UserThread newUser = new UserThread(socket, this);
userThreads.add(newUser);
newUser.start();

}

} catch (IOException ex) {
System.out.println("Error in the server: " + ex.getMessage());
ex.printStackTrace();
}
}

static class ReadCommand implements Runnable
    {
    private final ChatServer server;

    public ReadCommand(ChatServer server)
        {
        this.server=server;
        }

    public void run()
        {
        Scanner in;
        while(true)
            {
            in = new Scanner(System.in);
            String input = in.nextLine();
            if (input.equals("quit"))
                {
                //make exit
                server.broadcast("quit",null);
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
 * Delivers a message from one user to others (broadcasting)
 */
void broadcast(String message, UserThread excludeUser) {
for (UserThread aUser : userThreads) {
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
 * When a client is disconneted, removes the associated username and UserThread
 */
void removeUser(String userName, UserThread aUser) {
boolean removed = userNames.remove(userName);
if (removed) {
userThreads.remove(aUser);
System.out.println("The user " + userName + " quitted");
}
}

Set<String> getUserNames() {
return this.userNames;
}

/**
 * Returns true if there are other users connected (not count the currently connected user)
 */
boolean hasUsers() {
return !this.userNames.isEmpty();
}
}