package chat.server;

import chat.client.Client;
import chat.server.command.Command;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Worker that listens for the client's messages.
 */
public class ServerWorker extends Thread {

    /**
     * The pattern that needs to be send as the start of a message to indicate
     * that the command is being sent.
     */
    private static final String COMMAND_PATTERN = "/";

    /** The reader that waits and reads the client's messages. */
    private BufferedReader serverReader;

    /** Writer used for writing messages to the client. */
    private PrintWriter serverWriter;

    /** The server side socket. */
    private Socket serverSocket;

    /** The server this thread is "working" for. */
    private ChatServer server;

    /** The username of the client this thread is dedicated to. */
    private String clientUsername;

    /** Maps the command name to the object that can execute the command. */
    private static Map<String, Command> commandMap = new HashMap<>();

    static {
        try {
            for(String fqcn : Files.readAllLines(Paths.get("res/command-list.txt"))) {
                if(fqcn.isBlank()) continue;

                try {
                    Command command = (Command) Class.forName(fqcn).getConstructor().newInstance();
                    System.out.println("Loaded command '" + command.getName() + "'");
                    commandMap.put(command.getName(), command);

                } catch (Exception e) {
                    System.err.println("Error instantiating command from '" + fqcn + "'.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading commands.");
        }
    }

    ServerWorker(ChatServer server, Socket serverSocket, String clientUsername) {
        try {
            this.server         = Objects.requireNonNull(server, "Server cannot be null.");
            this.serverSocket   = Objects.requireNonNull(serverSocket, "Server socket cannot be null.");
            this.clientUsername = Objects.requireNonNull(clientUsername, "Client username cannot be null.");

            serverWriter = new PrintWriter(serverSocket.getOutputStream(), true);
            serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            server.addServerWorker(this);

            String clientRank = Client.RANK[ server.getClientPrivilegeLevel(clientUsername) ];
            server.sendGlobalServerMessage(clientRank + " " + clientUsername + " has just connected!");

            setName("ServerWorker " + clientUsername);
            setDaemon(true);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = serverReader.readLine()) != null) {
                processMessage(message);
            }
        } catch (SocketException ex) {
            System.out.println("gasim!");

        } catch (IOException ex) {
            System.out.println("IO Exc");
        }

        server.removeServerWorker(this);
        closeConnection();
    }

    private void closeConnection() {
        try {
            serverReader.close();
            serverWriter.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processMessage(String message) throws IOException {
        if(message.startsWith(COMMAND_PATTERN)) {
            // Remove the first character "/" and trim to avoid space "holes".
            processCommand(message.substring(1).trim());
        } else {
            server.sendGlobalClientMessage(clientUsername, message);
        }
    }

    private void processCommand(String message) throws IOException {
        if(message.isEmpty()) {
            server.sendPrivateServerMessage(clientUsername, "Invalid, empty command.");
            return;
        }

        int firstSpace = message.indexOf(' ');
        String commandName = (firstSpace >= 0) ? message.substring(0, firstSpace) : message;

        Command command = commandMap.get(commandName);
        if(command == null) {
            String msg = "Invalid command '" + commandName + "'. For a list of valid commands, type /help";
            server.sendPrivateServerMessage(clientUsername, msg);
            return;
        }

        if(server.getClientPrivilegeLevel(clientUsername) < command.getRequiredPrivilegeLevel()) {
            String msg = "You don't have the required privilege to perform the '" + commandName + "' command.";
            server.sendPrivateServerMessage(clientUsername, msg);
            return;
        }

        String[] args = (firstSpace >= 0) ? message.substring(message.indexOf(' ')).trim().split("\\s+") : new String[0];
        command.execute(args, server, this);
    }

    //===========================================================
    //                        Getters
    //===========================================================

    public static Map<String, Command> getCommandMap() {
        return commandMap;
    }

    public PrintWriter getServerWriter() {
        return serverWriter;
    }

    public String getClientUsername() {
        return clientUsername;
    }

    @Override
    public String toString() {
        return clientUsername;
    }
}