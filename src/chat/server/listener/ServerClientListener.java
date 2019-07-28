package chat.server.listener;

/**
 * Models objects that listen for server client events.
 */
public interface ServerClientListener {

    /**
     * Performed once a new client connects to the server.
     */
    void clientConnected(String username);

    /**
     * Performed once a new client disconnects from the server.
     */
    void clientDisconnected(String username);
}
