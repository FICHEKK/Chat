package chat.server;

import chat.client.Client;
import chat.server.dao.DAO;
import chat.server.dao.DAOProvider;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * Models the chat-server that receives client's messages,
 * processes them and sends the response.
 */
public class ChatServer extends AbstractChatServer {

    /** The maximum number of clients of this server. */
    private final int maxClients;

    /** The socket of this server used to connect with incoming clients. */
    private ServerSocket acceptingSocket;

    /** The DAO instance used by the server. */
    private DAO dao = DAOProvider.getInstance().getDAO();

    //===========================================================
    //                      Constructors
    //===========================================================

    /**
     * Constructs a new {@link ChatServer} object, with specified client
     * limit.
     *
     * @param maxClients the maximum number of clients
     */
    public ChatServer(int maxClients) {
        this.maxClients = maxClients;
    }

    //===========================================================
    //                      Public API
    //===========================================================

    @Override
    public void start(int port) {
        try {
            acceptingSocket = new ServerSocket(port);
            notifyServerStarted();

            while (true) {
                Socket serverSocket = acceptingSocket.accept();
                processClientConnection(serverSocket);
            }
        } catch (SocketException e) {
            notifyServerClosed();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes a single client connection and determines if the connection
     * should be accepted or denied.
     * @param serverSocket the server socket used for sending connection status flags
     *                     to the client socket
     * @throws IOException if an IO error occurs
     */
    private void processClientConnection(Socket serverSocket) throws IOException {
        int request = serverSocket.getInputStream().read();

        if(request == Client.LOGIN_REQUEST) {
            processLoginRequest(serverSocket);

        } else if(request == Client.REGISTRATION_REQUEST) {
            processRegistrationRequest(serverSocket);

        } else {
            System.err.println("Invalid client request. Closing client connection.");
            serverSocket.close();
        }
    }

    private void processLoginRequest(Socket serverSocket) throws IOException {
        if(workers.size() >= maxClients) {
            notifyServerMessage("Connection denied: Client limit reached.");
            serverSocket.getOutputStream().write(LOGIN_DENIED_SERVER_FULL);
            serverSocket.close();
            return;
        }

        serverSocket.getOutputStream().write(LOGIN_ESTABLISHED_SERVER_NOT_FULL);

        // Reader on the server side that reads the client's messages.
        BufferedReader serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        String username = serverReader.readLine();
        String password = serverReader.readLine();

        if(isClientOnline(username)) {
            serverSocket.getOutputStream().write(LOGIN_DENIED_CLIENT_ALREADY_LOGGED_IN);
            serverSocket.close();
            return;
        }

        boolean registered = dao.isClientRegistered(username);
        if(!registered) {
            serverSocket.getOutputStream().write(LOGIN_DENIED_USERNAME_NOT_REGISTERED);
            serverSocket.close();
            return;
        }

        boolean successfulLogin = dao.loginClient(username, password);
        if(!successfulLogin) {
            serverSocket.getOutputStream().write(LOGIN_DENIED_WRONG_PASSWORD);
            serverSocket.close();
            return;
        }

        boolean clientBanned = dao.isClientBanned(username);
        if(clientBanned) {
            serverSocket.getOutputStream().write(LOGIN_DENIED_CLIENT_IS_BANNED);
            serverSocket.close();
            return;
        }

        // Every test was passed, send the accepting flag and create a new worker thread.
        serverSocket.getOutputStream().write(LOGIN_ACCEPTED);
        new ServerWorker(this, serverSocket, username).start();
    }

    private void processRegistrationRequest(Socket clientSocket) throws IOException {
        BufferedReader clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String username = clientReader.readLine();
        String password = clientReader.readLine();

        if(dao.isClientRegistered(username)) {
            notifyServerMessage("Registration denied: username '" + username + "' already taken.");
            clientSocket.getOutputStream().write(REGISTRATION_FAILED_USERNAME_ALREADY_TAKEN);
            return;
        }

        boolean successful = dao.registerClient(username, password);

        if(successful) {
            clientSocket.getOutputStream().write(REGISTRATION_SUCCEEDED);
            notifyServerMessage("New client '" + username + "' has just registered!");
        } else {
            clientSocket.getOutputStream().write(REGISTRATION_FAILED_IO_ERROR);
            notifyServerMessage("Registration denied: IO error occurred.");
        }

        clientSocket.close();
    }

    public void close() {
        new Thread(() -> {
            try {
                sendGlobalServerMessage("Server is closing...");
                acceptingSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    //===========================================================
    //                    Server actions
    //===========================================================

    /**
     * Un-bans the client with the specified username.
     * @param unbanner the client that performs the un-banning
     * @param username the username of the client to be un-banned
     * @return {@code true} if the client was successfully un-banned,
     *         {@code false} otherwise
     */
//    public boolean unban(String unbanner, String username) {
//        boolean didUnban = dao.unbanClient(username);
//
//        if(!didUnban) return false;
//
//        sendGlobalServerMessage("'" + username + "' has been un-banned by '" + unbanner + "'.");
//        return true;
//    }

//    public boolean delete(String deleter, String username) {
//        boolean didDelete = dao.deleteClient(username);
//
//        if(!didDelete) return false;
//
//        sendGlobalServerMessage("'" + username + "' has been deleted by '" + deleter + "'.");
//        return true;
//    }

    //===========================================================
    //                    Sending messages
    //===========================================================

    /**
     * Sends the private message from one client to the specified client.
     * The original message is also sent to the sender client in order to
     * notify the sender that the message was successfully sent.
     * @param sender the username of the private message sender
     * @param receiver the username of the private message receiver
     * @param message the private message to be sent
     */
    public void sendPrivateClientMessage(String sender, String receiver, String message) {
        for(ServerWorker worker : workers) {
            if(worker.getClientUsername().equals(sender) || worker.getClientUsername().equals(receiver)) {
                worker.getServerWriter().write(INCOMING_PRIVATE_CLIENT_MESSAGE);
                worker.getServerWriter().println(sender);
                worker.getServerWriter().println(receiver);
                worker.getServerWriter().println(message);
            }
        }

        notifyServerMessage(sender + " sent \"" + message + "\" to " + receiver);
    }

    /**
     * Sends the private message from server to the specified client.
     * @param receiver the username of the private message receiver
     * @param message the private message to be sent
     */
    public void sendPrivateServerMessage(String receiver, String message) {
        for(ServerWorker worker : workers) {
            if(worker.getClientUsername().equals(receiver)) {
                worker.getServerWriter().write(INCOMING_PRIVATE_SERVER_MESSAGE);
                worker.getServerWriter().println(message);
                break;
            }
        }
    }

    /**
     * Sends the given client message to all of the clients connected to the server.
     * @param sender the username of the sender
     * @param message the client message to be broadcast
     */
    public void sendGlobalClientMessage(String sender, String message) {
        broadcast(INCOMING_GLOBAL_CLIENT_MESSAGE, sender, message);
        notifyServerMessage("[" + sender + "] " + message);
    }

    /**
     * Sends the given server message to all of the clients connected to the server.
     * @param message the message to be sent
     */
    public void sendGlobalServerMessage(String message) {
        broadcast(INCOMING_GLOBAL_SERVER_MESSAGE, message);
        notifyServerMessage("[SERVER] " + message);
    }

    public void broadcastClientDisconnected(String username) {
        broadcast(INCOMING_DISCONNECT_MESSAGE, "'" + username + "' has disconnected from the server.");
    }

    public void broadcastClientKicked(String kicker, String kicked) {
        broadcast(INCOMING_KICK_MESSAGE, "'" + kicked + "' was kicked from the server by '" + kicker + "'.");
    }

    public void broadcastClientBanned(String banner, String banned) {
        broadcast(INCOMING_BAN_MESSAGE, "'" + banned + "' was banned from the server by '" + banner + "'.");
    }

    public void broadcastClientDeleted(String deleter, String deleted) {
        broadcast(INCOMING_DELETE_MESSAGE, "'" + deleted + "' was deleted by '" + deleter + "'.");
    }

    /**
     * Broadcasts the given flag followed by the given sequence of messages
     * to all of the clients currently connected to the server.
     * @param flag the flag to be sent
     * @param messages a sequence of messages to be broadcast
     */
    private void broadcast(int flag, String... messages) {
        for(ServerWorker worker : workers) {
            worker.getServerWriter().write(flag);

            for(String message : messages) {
                worker.getServerWriter().println(message);
            }
        }
    }

    //===========================================================
    //                      DAO adapter
    //===========================================================

    public boolean banClient(String username) {
        return dao.banClient(username);
    }

    public boolean unbanClient(String username) {
        return dao.unbanClient(username);
    }

    public boolean deleteClient(String username) {
        return dao.deleteClient(username);
    }

    public boolean isClientRegistered(String username) {
        return dao.isClientRegistered(username);
    }

    public boolean isClientBanned(String username) {
        return dao.isClientBanned(username);
    }

    public int getClientPrivilegeLevel(String username) {
        return dao.getClientPrivilegeLevel(username);
    }

    public boolean setClientPrivilegeLevel(String username, int privilegeLevel) {
        return dao.setClientPrivilegeLevel(username, privilegeLevel);
    }

    public List<String> getBanList() {
        return dao.getBanList();
    }

    //===========================================================
    //                        Getters
    //===========================================================

    public String getAddress() {
        return acceptingSocket.getInetAddress().toString();
    }

    public int getPort() {
        return acceptingSocket.getLocalPort();
    }

    public int getMaxClients() {
        return maxClients;
    }
}