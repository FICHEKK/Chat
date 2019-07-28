package chat.client;

import chat.client.listener.ClientListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract implementation of the chat-client that implements:
 * <ul>
 *     <li>Listener addition and removal.</li>
 *     <li>Listener notifying.</li>
 *     <li>Private and global, client and server message receiving.</li>
 * </ul>
 */
abstract class AbstractChatClient implements Client {

    /** A collection of listeners of this client. */
    private List<ClientListener> listeners = new ArrayList<>();

    //===========================================================
    //              Adding and removing listeners
    //===========================================================

    public void addClientListener(ClientListener listener) {
        if(!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    void removeClientListener(ClientListener listener) {
        listeners.remove(listener);
    }

    //===========================================================
    //                  Receiving messages
    //===========================================================

    void receivePrivateClientMessage(String sender, String receiver, String message) {
        notifyPrivateClientMessageReceived(sender, receiver, message);
    }

    void receivePrivateServerMessage(String message) {
        notifyPrivateServerMessageReceived(message);
    }

    void receiveGlobalClientMessage(String sender, String message) {
        notifyGlobalClientMessageReceived(sender, message);
    }

    void receiveGlobalServerMessage(String message) {
        notifyGlobalServerMessageReceived(message);
    }

    //===========================================================
    //                  Notifying listeners
    //===========================================================

    private void notifyPrivateClientMessageReceived(String sender, String receiver, String message) {
        listeners.forEach(l -> l.privateClientMessageReceived(sender, receiver, message));
    }

    void notifyPrivateServerMessageReceived(String message) {
        listeners.forEach(l -> l.privateServerMessageReceived(message));
    }

    private void notifyGlobalClientMessageReceived(String sender, String message) {
        listeners.forEach(l -> l.globalClientMessageReceived(sender, message));
    }

    private void notifyGlobalServerMessageReceived(String message) {
        listeners.forEach(l -> l.globalServerMessageReceived(message));
    }

    void notifyOnServerConnect() {
        listeners.forEach(ClientListener::onServerConnect);
    }

    void notifyOnServerDisconnect() {
        listeners.forEach(ClientListener::onServerDisconnect);
    }

    void notifyOnServerKick(String kicker) {
        listeners.forEach(l -> l.onServerKick(kicker));
    }

    void notifyOnServerBan(String banner) {
        listeners.forEach(l -> l.onServerBan(banner));
    }

    void notifyOnServerDelete(String deleter) {
        listeners.forEach(l -> l.onServerDelete(deleter));
    }
}