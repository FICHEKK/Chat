package chat.server;

import chat.server.listener.ServerClientListener;
import chat.server.listener.ServerEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

abstract class AbstractChatServer implements Server {

    /** A list of workers of this server. Each worker represents a single client. */
    List<ServerWorker> workers = new ArrayList<>();

    /** A list of server event listeners. */
    private List<ServerEventListener> eventListeners = new ArrayList<>();

    /** A list of server client event listeners. */
    private List<ServerClientListener> clientListeners = new ArrayList<>();

    //===========================================================
    //                    Server workers
    //===========================================================

    /**
     * Adds a new {@link ServerWorker} to the list of all the workers.
     * @param worker the worker to be added
     */
    void addServerWorker(ServerWorker worker) {
        Objects.requireNonNull(worker, "Worker cannot be null.");

        String username = worker.getClientUsername();
        if(isClientOnline(username)) {
            System.err.println("User '" + username + "' is already added");
        } else {
            workers.add(worker);
            notifyClientConnected(username);
        }
    }

    /**
     * Removes the given {@link ServerWorker} from the list of all the workers.
     * @param worker the worker to be removed
     */
    void removeServerWorker(ServerWorker worker) {
        boolean didRemove = workers.remove(worker);

        if(didRemove) {
            notifyClientDisconnected(worker.getClientUsername());
        }
    }

    //===========================================================
    //                    Server information
    //===========================================================

    /**
     * Returns the reference to the {@link ServerWorker} that represents
     * the client with the specified username.
     * @param username the username of the client
     * @return reference to the {@link ServerWorker} that represents the client with the specified username,
     *         or {@code null} if the requested client is not online
     */
    public ServerWorker getOnlineClient(String username) {
        for(ServerWorker worker : workers) {
            if(worker.getClientUsername().equals(username)) {
                return worker;
            }
        }

        return null;
    }

    /**
     * Checks if the client with the specified username is online.
     * @param username the username of the requested client
     * @return {@code true} if the client is online, {@code false} otherwise
     */
    public boolean isClientOnline(String username) {
        return getOnlineClient(username) != null;
    }

    //===========================================================
    //               Adding and removing listeners
    //===========================================================

    public void addServerListener(ServerEventListener l) {
        if(!eventListeners.contains(l)) {
            eventListeners.add(l);
        }
    }

    void removeServerListener(ServerEventListener l) {
        eventListeners.remove(l);
    }

    public void addServerClientListener(ServerClientListener l) {
        if(!clientListeners.contains(l)) {
            clientListeners.add(l);
        }
    }

    void removeServerClientListener(ServerClientListener l) {
        clientListeners.remove(l);
    }

    //===========================================================
    //                    Listener handling
    //===========================================================

    void notifyServerStarted() {
        eventListeners.forEach(ServerEventListener::serverStarted);
    }

    void notifyServerClosed() {
        eventListeners.forEach(ServerEventListener::serverClosed);
    }

    void notifyServerMessage(String message) {
        eventListeners.forEach(l -> l.serverMessage(message));
    }

    private void notifyClientConnected(String username) {
        clientListeners.forEach(l -> l.clientConnected(username));
    }

    private void notifyClientDisconnected(String username) {
        clientListeners.forEach(l -> l.clientDisconnected(username));
    }

    //===========================================================
    //                         Getters
    //===========================================================

    public int getClientCount() {
        return workers.size();
    }

    public List<ServerWorker> getWorkers() {
        return workers;
    }
}