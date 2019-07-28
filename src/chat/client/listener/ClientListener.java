package chat.client.listener;

/**
 * Models objects that listen for the client events.
 */
public interface ClientListener {

    /**
     * Called once the client successfully connects to
     * the server.
     */
    void onServerConnect();

    /**
     * Called once the client successfully disconnects
     * from the server.
     */
    void onServerDisconnect();

    /**
     * Called once the client has been kicked from the server.
     */
    void onServerKick(String kicker);

    /**
     * Called once the client has been banned from the server.
     */
    void onServerBan(String banner);

    /**
     * Called once the client has been deleted from the server.
     */
    void onServerDelete(String deleter);

    /**
     * Processes the received private client message.
     * @param sender the sender of the private message
     * @param receiver the receiver of the private message
     * @param message the message
     */
    void privateClientMessageReceived(String sender, String receiver, String message);

    /**
     * Processes the received private server message.
     * @param message the message
     */
    void privateServerMessageReceived(String message);

    /**
     * Processes the received global client message.
     * @param sender the sender of the global message
     * @param message the message
     */
    void globalClientMessageReceived(String sender, String message);

    /**
     * Processes the received global server message.
     * @param message the message
     */
    void globalServerMessageReceived(String message);
}
