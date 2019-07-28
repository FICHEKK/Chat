package chat.server;

/**
 * Models objects that encapsulate the server behaviour.
 */
public interface Server {

    //===========================================================
    //              Login flags sent to the client
    //===========================================================

    /**
     * Response value sent once the input/output error has occurred.
     */
    byte LOGIN_DENIED_IO_ERROR = 32;

    /**
     * Response value sent once the client connection has been established.
     * Connection being established does not mean that the connection has yet
     * been accepted, rather that the connection passed the first "connection
     * test".
     */
    byte LOGIN_ESTABLISHED_SERVER_NOT_FULL = 33;

    /**
     * Response value sent once the client connection has been denied.
     * Reason of connection denial is that the server is full (maximum
     * number of clients reached).
     */
    byte LOGIN_DENIED_SERVER_FULL = 34;

    /**
     * Response value sent only if the connection has already been established
     * and if the client with the provided username is not already logged in.
     * Getting this flag as a response means that the connection has been
     * successfully accepted.
     */
    byte LOGIN_ACCEPTED = 35;

    /**
     * Response value sent once the client connection has been denied.
     * Reason of connection denial is that the client with the provided
     * username is already logged in.
     */
    byte LOGIN_DENIED_CLIENT_ALREADY_LOGGED_IN = 36;

    /**
     * Response value sent once the client whose username is on the ban-list
     * tries to connect to the server.
     */
    byte LOGIN_DENIED_CLIENT_IS_BANNED = 37;

    /**
     * Response value sent to the client once the wrong password has been inserted.
     */
    byte LOGIN_DENIED_WRONG_PASSWORD = 38;

    /**
     * Response value sent to the client once the username that is not registered
     * has been inserted.
     */
    byte LOGIN_DENIED_USERNAME_NOT_REGISTERED = 39;

    //===========================================================
    //          Registration flags sent to the client
    //===========================================================

    /**
     * Response sent to the client once the input/output error occurs
     * during the registration of a new account.
     */
    byte REGISTRATION_FAILED_IO_ERROR = 40;

    /**
     * Response sent to the client once the client tries to register
     * a new account with the username that is already taken.
     */
    byte REGISTRATION_FAILED_USERNAME_ALREADY_TAKEN = 41;

    /**
     * Response sent to the client once the registration was successful.
     */
    byte REGISTRATION_SUCCEEDED = 42;

    //===========================================================
    //             Message flags sent to the client
    //===========================================================

    /**
     * Value sent to the client indicating that the private client message will be sent.
     */
    byte INCOMING_PRIVATE_CLIENT_MESSAGE = 43;

    /**
     * Value sent to the client indicating that the private server message will be sent.
     */
    byte INCOMING_PRIVATE_SERVER_MESSAGE = 44;

    /**
     * Value sent to the client indicating that the global client message will be sent.
     */
    byte INCOMING_GLOBAL_CLIENT_MESSAGE = 45;

    /**
     * Value sent to the client indicating that the global server message will be sent.
     */
    byte INCOMING_GLOBAL_SERVER_MESSAGE = 46;

    /**
     * Value sent to the client indicating that some client has disconnected from the
     * server.
     */
    byte INCOMING_DISCONNECT_MESSAGE = 47;

    /**
     * Value sent to the client indicating that some client was kicked and the message
     * containing the kick information will be sent.
     */
    byte INCOMING_KICK_MESSAGE = 48;

    /**
     * Value sent to the client indicating that some client was banned and the message
     * containing the ban information will be sent.
     */
    byte INCOMING_BAN_MESSAGE = 49;

    /**
     * Value sent to the client indicating that some client was deleted and the message
     * containing the ban information will be sent.
     */
    byte INCOMING_DELETE_MESSAGE = 50;

    //===========================================================
    //             Kick and ban flags sent to the client
    //===========================================================

    /**
     * Value sent to the client indicating that the receiving client has been kicked.
     */
    byte KICKED = 51;

    /**
     * Value sent to the client indicating that the receiving client has been banned.
     */
    byte BANNED = 52;

    /**
     * Value sent to the client indicating that the receiving client has been deleted.
     */
    byte DELETED = 53;

    //===========================================================
    //                       Server API
    //===========================================================

    /**
     * Starts the server on the given port and waits for the incoming
     * clients.
     *
     * @param port the port of the server
     */
    void start(int port);

    /**
     * Closes the server socket, disabling further client connections.
     */
    void close();
}