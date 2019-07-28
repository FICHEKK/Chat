package chat.server.dao;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * DAO implementation that uses files as a means of saving
 * client data to the persistent memory.
 */
public class FileDAO implements DAO {

    /** Path to the file containing all of the registered clients' data. */
    private static final Path clientListPath = Paths.get("res/client-list.txt");
    
    /**
     * A map that maps the unique client's username to its attributes container. <br>
     * Visually, it looks like this: USERNAME -> (PASSWORD, PRIVILEGE_LEVEL, IS_BANNED)
     */
    private Map<String, ClientAttributes> clientMap = new HashMap<>();


    //===========================================================
    //                    Constructor
    //===========================================================

    FileDAO() {
        try {
            if(!Files.exists(clientListPath)) {
                Files.createFile(clientListPath);
            }
            loadClientMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadClientMap() throws IOException {
        int clientsLoaded = 0;

        for(String clientLine : Files.readAllLines(clientListPath)) {
            String[] parts = clientLine.split(",");

            if(parts.length != 4) {
                System.err.println("Client line '" + clientLine + "' is corrupted.");
                continue;
            }

            String username = parts[0];
            String password = parts[1];
            int privilegeLevel = Integer.parseInt(parts[2]);
            boolean isBanned = Boolean.parseBoolean(parts[3]);

            clientMap.put(username, new ClientAttributes(password, privilegeLevel, isBanned));
            clientsLoaded++;
        }

        System.out.println("Loaded " + clientsLoaded + " clients.");
    }

    //===========================================================
    //                    Public API
    //===========================================================

    @Override
    public boolean loginClient(String username, String password) {
        if(!isClientRegistered(username)) return false;
        return password.equals( clientMap.get(username).password );
    }

    @Override
    public boolean registerClient(String username, String password) {
        if(isClientRegistered(username)) {
            System.err.println("Client '" + username + "' is already registered.");
            return false;
        }

        try {
            Files.write(clientListPath, (username + "," + password + ",0,false\r\n").getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Could not create the new client '" + username + ".");
            return false;
        }
        clientMap.put(username, new ClientAttributes(password, 0, false));
        return true;
    }

    @Override
    public boolean isClientRegistered(String username) {
        return clientMap.containsKey(username);
    }

    @Override
    public boolean isClientBanned(String username) {
        if(!isClientRegistered(username)) return false;
        return clientMap.get(username).isBanned;
    }

    @Override
    public int getClientPrivilegeLevel(String username) {
        if(!isClientRegistered(username)) return -1;
        return clientMap.get(username).privilegeLevel;
    }

    @Override
    public boolean setClientPrivilegeLevel(String username, int privilegeLevel) {
        if(!isClientRegistered(username)) {
            System.err.println("Could not change privilege level of client '" + username + "' as the client is not registered.");
            return false;
        }

        ClientAttributes attributes = clientMap.get(username);
        attributes.privilegeLevel = privilegeLevel;
        return updateExistingClient(username, attributes);
    }

    @Override
    public boolean deleteClient(String username) {
        if(!isClientRegistered(username)) {
            System.err.println("Could not delete '" + username + "' as the client is not registered.");
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(clientListPath);

            for(int i = 0, len = lines.size(); i < len; i++) {
                if(lines.get(i).startsWith(username)) {
                    lines.remove(i);
                    break;
                }
            }

            Files.write(clientListPath, lines);
        } catch (IOException e) {
            System.err.println("Could not delete the given client '" + username + "'.");
            return false;
        }

        clientMap.remove(username);
        return true;
    }

    @Override
    public boolean banClient(String username) {
        ClientAttributes attributes = clientMap.get(username);
        attributes.isBanned = true;
        return updateExistingClient(username, attributes);
    }

    @Override
    public boolean unbanClient(String username) {
        if(!isClientRegistered(username)) {
            System.err.println("Could not un-ban client '" + username + "' as the client is not registered.");
            return false;
        }

        if(!isClientBanned(username)) {
            System.err.println("Client '" + username + "' is already un-banned.");
            return false;
        }

        ClientAttributes attributes = clientMap.get(username);
        attributes.isBanned = false;
        return updateExistingClient(username, attributes);
    }

    @Override
    public List<String> getBanList() {
        List<String> banList = new ArrayList<>();

        clientMap.forEach((username, attr) -> {
            if(attr.isBanned) {
                banList.add(username);
            }
        });

        return banList;
    }

    /**
     * Updates the attributes of the client with the given username.
     * @param username the client's username
     * @param attr the new client's attributes
     * @return {@code true} if the update was performed successfully,
     *         {@code false} otherwise
     */
    private boolean updateExistingClient(String username, ClientAttributes attr) {
        try {
            List<String> lines = Files.readAllLines(clientListPath);

            for(int i = 0, len = lines.size(); i < len; i++) {
                if(lines.get(i).startsWith(username)) {
                    lines.set(i, username + "," + attr.password + "," + attr.privilegeLevel + "," + attr.isBanned);
                    break;
                }
            }

            Files.write(clientListPath, lines);
        } catch (IOException e) {
            System.err.println("Could not update the given client '" + username + "'.");
            return false;
        }

        clientMap.put(username, attr);
        return true;
    }

    //===========================================================
    //          ClientAttributes helper data structure
    //===========================================================

    /**
     * A simple helper data structure that encapsulates single
     * client's data. The username field is not included as it is
     * the key which maps to this structure, making the username
     * field redundant.
     */
    private static class ClientAttributes {

        /** The client's password. */
        private String password;

        /** The client's privilege level. */
        private int privilegeLevel;

        /** The flag that indicated whether the client is banned. */
        private boolean isBanned;

        /**
         * Constructs a new client data container.
         * @param password the client's password
         * @param privilegeLevel the client's privilege level
         * @param isBanned the flag that indicated whether the client is banned
         */
        private ClientAttributes(String password, int privilegeLevel, boolean isBanned) {
            this.password = password;
            this.privilegeLevel = privilegeLevel;
            this.isBanned = isBanned;
        }
    }
}