package chat.client;

import chat.server.Server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Objects;

/**
 * Encapsulates the data of a single chat-client.
 */
public class ChatClient extends AbstractChatClient {

    /** The client's username. */
    private String username;

    /** The client's password. */
    private String password;

    /** This client's socket. */
    private Socket clientSocket;

    /** The writer used by the client for writing text to the server. */
    private PrintWriter clientWriter;

    //===========================================================
    //                      Constructor
    //===========================================================

    /**
     * Creates a new client with the given username.
     *
     * @param username the username of the client; cannot be null
     * @param password the password of the client; cannot be null
     */
    public ChatClient(String username, String password) {
        this.username = Objects.requireNonNull(username, "Username cannot be null.");
        this.password = Objects.requireNonNull(password, "Password cannot be null.");
    }

    //===========================================================
    //                      Public API
    //===========================================================

    @Override
    public int login(String address, int port) {
        try {
            clientSocket = new Socket(address, port);
            clientSocket.getOutputStream().write(LOGIN_REQUEST);

            // Checking if the server is full.
            int status = clientSocket.getInputStream().read();
            if (status != Server.LOGIN_ESTABLISHED_SERVER_NOT_FULL) return status;

            // Sending the client username and password to the server.
            clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriter.println(username);
            clientWriter.println(password);

            // Checking if provided username is accepted by the server.
            status = clientSocket.getInputStream().read();
            if (status != Server.LOGIN_ACCEPTED) return status;

            // Every test was passed, create a new thread for the client.
            new ClientWorker(this, clientSocket).start();
            notifyOnServerConnect();
            return Server.LOGIN_ACCEPTED;

        } catch (IOException e) {
            return Server.LOGIN_DENIED_IO_ERROR;
        }
    }

    @Override
    public int register(String address, int port) {
        try {
            clientSocket = new Socket(address, port);
            clientSocket.getOutputStream().write(REGISTRATION_REQUEST);

            // Sending the client username and password to the server.
            clientWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            clientWriter.println(username);
            clientWriter.println(password);

            // Return the registration status.
            return clientSocket.getInputStream().read();

        } catch (IOException e) {
            return Server.REGISTRATION_FAILED_IO_ERROR;
        }
    }

    @Override
    public void disconnect() {
        if(closeConnection()) notifyOnServerDisconnect();
    }

    void receiveKick(String kicker) {
        if(closeConnection()) notifyOnServerKick(kicker);
    }

    void receiveBan(String banner) {
        if(closeConnection()) notifyOnServerBan(banner);
    }

    void receiveDelete(String deleter) {
        if(closeConnection()) notifyOnServerDelete(deleter);
    }

    /**
     * Closes the client side socket.
     * @return {@code true} if the socket was closed successfully,
     *         {@code false} otherwise
     */
    private boolean closeConnection() {
        try {
            clientSocket.close();
            return true;
        } catch (IOException e) {
            notifyPrivateServerMessageReceived("Could not disconnect from the server.");
            return false;
        }
    }

    //===========================================================
    //                    Sending message
    //===========================================================

    /**
     * Sends the given message to the server.
     * @param message the message to be sent
     */
    public void sendMessage(String message) {
        clientWriter.println(message);
    }

    //===========================================================
    //                        Getters
    //===========================================================

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return clientSocket.getInetAddress().toString();
    }

    public int getPort() {
        return clientSocket.getPort();
    }

    //===========================================================
    //                hashCode, equals, toString
    //===========================================================

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatClient client = (ChatClient) o;
        return username.equals(client.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
}