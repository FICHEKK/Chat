package chat.client.frame;

import chat.Common;
import chat.JHintTextField;
import chat.client.ChatClient;
import chat.client.listener.ClientListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Models the client's view, that is, the graphical user
 * interface that the client sees and interacts with.
 */
public class ChatClientView extends JFrame implements ClientListener {

    /** The chat text-pane. */
    private JTextPane chatTP;

    /** The message text field. */
    private JHintTextField messageTF;

    /** The button used for sending the message. */
    private JButton sendButton;

    /** The client of this client view. */
    private ChatClient client;

    /** The color of the send message button. */
    private static final Color SEND_MESSAGE_BUTTON_COLOR = new Color(0, 156, 255);

    //===========================================================
    //                     Creating GUI
    //===========================================================

    /**
     * Constructs a new client view for the given client.
     * @param client the client model
     */
    ChatClientView(ChatClient client) {
        this.client = client;

        setTitle("Chat Client");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                client.disconnect();
            }
        });

        initGUI();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);

        // Add listener after the GUI has been initialized.
        this.client.addClientListener(this);
    }

    private void initGUI() {
        Container pane = getContentPane();
        pane.add(createServerDisconnectPanel(), BorderLayout.NORTH);
        pane.add(createChatPane(), BorderLayout.CENTER);
        pane.add(createMessagePanel(), BorderLayout.SOUTH);
    }

    private JPanel createServerDisconnectPanel() {
        JPanel serverDisconnectPanel = new JPanel(new GridLayout(1, 0));
        serverDisconnectPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        serverDisconnectPanel.setBackground(Color.black);

        JLabel info = new JLabel("Connected as " + client.getUsername() +
                                     " on " + client.getAddress() + ":" + client.getPort(), JLabel.CENTER);
        info.setForeground(Color.white);
        serverDisconnectPanel.add(info);

        return serverDisconnectPanel;
    }

    private JScrollPane createChatPane() {
        chatTP = new JTextPane();
        chatTP.setEditable(false);
        chatTP.setMargin(new Insets(10, 10, 10, 10));
        Common.textPaneAppend(
                chatTP,
                "Welcome, " + client.getUsername() + "!\r\n" +
                "For a list of all the valid commands, type /help\r\n",
                Common.PRIVATE_SERVER_MESSAGE_COLOR
        );
        return new JScrollPane(chatTP);
    }

    private JPanel createMessagePanel() {
        messageTF = new JHintTextField("Type something...");
        messageTF.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {
                    send(messageTF.getText());
                }
            }
        });
        messageTF.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));

        sendButton = new JButton("Send");
        sendButton.addActionListener(l -> send(messageTF.getText()));
        sendButton.setBorder(BorderFactory.createEmptyBorder());
        sendButton.setMargin(new Insets(8, 10, 8, 10));
        sendButton.setForeground(Color.white);
        sendButton.setBackground(SEND_MESSAGE_BUTTON_COLOR);

        JPanel messagePanel = new JPanel(new GridBagLayout());
        messagePanel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        messagePanel.add(messageTF, gbc);

        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 20;
        messagePanel.add(sendButton, gbc);

        return messagePanel;
    }

    /**
     * Sends the current text from the {@link #messageTF} to the server,
     * where the text will get processed and interpreted.
     * @param message the message to be sent
     */
    private void send(String message) {
        if(message.isBlank()) return;
        client.sendMessage(message);
        messageTF.setText("");
    }

    //===========================================================
    //                 Client Listener methods
    //===========================================================

    @Override
    public void onServerConnect() { }

    @Override
    public void onServerDisconnect() {
        dispose();
    }

    @Override
    public void onServerKick(String kicker) {
        Common.textPaneAppend(chatTP, "You were kicked from the server by '" + kicker + "'.\r\n", Common.CLIENT_KICKED_COLOR);
        messageTF.setEnabled(false);
        sendButton.setEnabled(false);
    }

    @Override
    public void onServerBan(String banner) {
        Common.textPaneAppend(chatTP, "You were banned from the server by '" + banner + "'.\r\n", Common.CLIENT_BANNED_COLOR);
        messageTF.setEnabled(false);
        sendButton.setEnabled(false);
    }

    @Override
    public void onServerDelete(String deleter) {
        Common.textPaneAppend(chatTP, "Your account was deleted by '" + deleter + "'.\r\n", Common.CLIENT_DELETED_COLOR);
        messageTF.setEnabled(false);
        sendButton.setEnabled(false);
    }

    @Override
    public void privateClientMessageReceived(String sender, String receiver, String message) {
        Common.textPaneAppend(
                chatTP,
                "[" + sender + " >>> " + receiver + "] " + message + "\r\n",
                Common.PRIVATE_CLIENT_MESSAGE_COLOR
        );
    }

    @Override
    public void privateServerMessageReceived(String message) {
        Common.textPaneAppend(chatTP, message + "\r\n", Common.PRIVATE_SERVER_MESSAGE_COLOR);
    }

    @Override
    public void globalClientMessageReceived(String sender, String message) {
        Common.textPaneAppend(chatTP, sender + ": " + message + "\r\n", Common.GLOBAL_CLIENT_MESSAGE_COLOR);
    }

    @Override
    public void globalServerMessageReceived(String message) {
        Common.textPaneAppend(chatTP, message + "\r\n", Common.GLOBAL_SERVER_MESSAGE_COLOR);
    }
}