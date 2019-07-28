package chat.client;

public interface Client {

    //===========================================================
    //                Client privilege levels
    //===========================================================

    /**
     * The low privilege level. This privilege level allows the
     * following actions:
     * <ul>
     *     <li>Sending private messages</li>
     *     <li>Sending global messages</li>
     * </ul>
     */
    int USER = 0;

    /**
     * The medium privilege level. This privilege level allows the
     * following actions:
     * <ul>
     *     <li>Kicking clients with privilege level {@link #USER}</li>
     *     <li>All {@link #USER} actions</li>
     * </ul>
     */
    int MODERATOR = 1;

    /**
     * The high privilege level. This privilege level allows the
     * following actions:
     * <ul>
     *     <li>Kicking and banning clients with privilege level {@link #MODERATOR} and lower</li>
     *     <li>Giving and removing {@link #MODERATOR} privilege level to/from {@link #USER} level clients</li>
     *     <li>All {@link #MODERATOR} actions</li>
     * </ul>
     */
    int ADMIN = 2;

    /**
     * The very high privilege level. This privilege level allows the
     * following actions:
     * <ul>
     *     <li>Kicking and banning clients with privilege level {@link #ADMIN} and lower</li>
     *     <li>Giving and removing {@link #ADMIN} privilege level to/from {@link #MODERATOR} and lower level clients</li>
     *     <li>All {@link #ADMIN} actions</li>
     * </ul>
     */
    int MASTER_ADMIN = 3;

    /**
     * The highest privilege level. This privilege level allows the
     * following actions:
     * <ul>
     *     <li>Kicking, banning and deleting clients with privilege level {@link #MASTER_ADMIN} and lower</li>
     *     <li>Giving and removing {@link #MASTER_ADMIN} privilege level to/from {@link #ADMIN} and lower level clients</li>
     *     <li>All {@link #MASTER_ADMIN} actions</li>
     * </ul>
     */
    int OWNER = 4;

    /**
     * An array of rank titles.
     */
    String[] RANK = {"User", "Moderator", "Admin", "Master Admin", "Owner"};

    //===========================================================
    //              Requests sent to the server
    //===========================================================

    /**
     * Value sent to the server indicating that the login request is being sent.
     */
    byte LOGIN_REQUEST = 3;

    /**
     * Value sent to the server indicating that the registration request is being sent.
     */
    byte REGISTRATION_REQUEST = 4;

    //===========================================================
    //                      Client API
    //===========================================================

    /**
     * Connects this client to the specified server, sending the login request.
     * If the client connected successfully, a new thread for receiving server
     * messages will be created.
     *
     * @param address the server address
     * @param port the server port
     * @return the login status
     */
    int login(String address, int port);

    /**
     * Connects this client to the specified server, sending the registration request
     * to the server.
     *
     * @param address the server address
     * @param port the server port
     * @return the registration status
     */
    int register(String address, int port);

    /**
     * Disconnects this client from the server.
     */
    void disconnect();
}
