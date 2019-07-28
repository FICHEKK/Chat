package chat.main;

import chat.server.frame.ChatServerView;

import javax.swing.*;

/**
 * Starts a new server process.
 */
public class ChatServerMain {

    /**
     * Server process starts from here.
     *
     * @param args none are used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatServerView().setVisible(true));
    }
}
