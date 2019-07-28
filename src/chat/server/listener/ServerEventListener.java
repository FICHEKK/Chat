package chat.server.listener;

/**
 * Models objects that listen for server events.
 */
public interface ServerEventListener {

    /**
     * Defines an action that should be performed on the
     * server starting.
     */
    void serverStarted();

    /**
     * Defines an action that should be performed on the
     * server closing.
     */
    void serverClosed();

    /**
     * Processes the message sent by the server
     *
     * @param message the server's message
     */
    void serverMessage(String message);
}
