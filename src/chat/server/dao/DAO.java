package chat.server.dao;

import java.util.List;

/**
 * The Data Access Object that offers the interface for storing
 * and changing client data in the persistent memory.
 */
public interface DAO {

    /**
     * Checks if the given username-password pair matches. If the given username
     * is not registered, {@code false} will be returned.
     * @param username the client's username
     * @param password the client's password
     * @return {@code true} if the log-in operation was successful, otherwise {@code false}
     */
    boolean loginClient(String username, String password);

    /**
     * Registers a new client with the given username and password.
     * @param username the username of the client
     * @param password the password of the client
     * @return {@code true} if the registration was successful,
     *         {@code false} if the username is already taken
     */
    boolean registerClient(String username, String password);

    /**
     * Checks if the given username is already registered.
     * @param username the username to be checked
     * @return {@code true} if the client with the given username is already registered,
     *         {@code false} otherwise
     */
    boolean isClientRegistered(String username);

    /**
     * Checks if the given username is on the ban-list.
     * @param username the username to be checked
     * @return {@code true} if the given username is on the ban-list, {@code false} otherwise
     */
    boolean isClientBanned(String username);

    /**
     * Fetches the privilege level of the specified client and returns it.
     * If the client with the given username does not exist, {@code -1} is returned.
     * @param username the username of the client
     * @return the client's privilege level, or {@code -1} if the client does not exist
     */
    int getClientPrivilegeLevel(String username);

    /**
     * Sets the specified client's privilege level
     * @param username the username of the client whose privilege is being changed
     * @param privilegeLevel the new privilege level
     * @return {@code true} if the client's privilege level was changed successfully,
     *         {@code false} if the client is not registered, or the given privilege level
     *         is the same as the current client's privilege level
     */
    boolean setClientPrivilegeLevel(String username, int privilegeLevel);

    /**
     * Deletes the client with the specified username.
     * @param username the username of the client to be removed
     * @return {@code true} if the client with the given username was deleted successfully,
     *         {@code false} if the client is not registered or a file write error occurs
     */
    boolean deleteClient(String username);

    /**
     * Bans the client with the given username.
     * @param username the username of the client to be banned
     * @return {@code true} if the client was banned successfully,
     *         {@code false} if the client is not registered, or is already banned
     */
    boolean banClient(String username);

    /**
     * Un-bans the client with the given username.
     * @param username the username of the client to be un-banned
     * @return {@code true} if the client was un-banned successfully,
     *         {@code false} if the client is not registered, or is already un-banned
     */
    boolean unbanClient(String username);

    /**
     * @return list of all the banned clients' usernames
     */
    List<String> getBanList();
}