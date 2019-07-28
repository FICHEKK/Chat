package chat.main;

import chat.client.frame.ChatClientLogin;

import javax.swing.*;

/**
 * Starts a new client process.
 */
public class ChatClientMain {

    /**
     * Client process starts from here.
     *
     * @param args none are used
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ChatClientLogin().setVisible(true));
    }
}
