package chat.server.frame;

import chat.server.ChatServer;
import chat.server.ServerWorker;
import chat.server.listener.ServerClientListener;

import javax.swing.*;

/**
 * A list model that holds all of the currently connected server clients.
 */
public class ServerWorkerListModel extends AbstractListModel<ServerWorker> implements ServerClientListener {

    /** The server model. */
    private ChatServer server;

    ServerWorkerListModel(ChatServer server) {
        this.server = server;
        this.server.addServerClientListener(this);
    }

    @Override
    public void clientConnected(String username) {
        this.fireIntervalAdded(server, 0, getSize());
    }

    @Override
    public void clientDisconnected(String username) {
        this.fireIntervalRemoved(server, 0, getSize());
    }

    @Override
    public int getSize() {
        return server.getWorkers().size();
    }

    @Override
    public ServerWorker getElementAt(int index) {
        return server.getWorkers().get(index);
    }
}
