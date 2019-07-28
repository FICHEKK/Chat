package chat.server.frame;

import chat.Common;
import chat.server.*;
import chat.server.listener.ServerClientListener;
import chat.server.listener.ServerEventListener;

import javax.swing.*;
import java.awt.*;

/**
 * Models the window that allows the administrator to start and close
 * server connection, while also receiving useful server state information.
 */
public class ChatServerView extends JFrame implements ServerEventListener, ServerClientListener {

    /** The server information label that displays the current state of the server. */
    private JLabel serverStateLabel;

    /** Label displaying the current number of online clients. */
    private JLabel clientStateLabel;

    /** Log text-pane used for displaying server state information. */
    private JTextPane logTP;

    /** Button used for starting the server. */
    private JButton startServerButton;

    /** Text-field used for inputting the server port. */
    private JTextField portTF;

    /** Text-field used for inputting the server client limit. */
    private JTextField maxClientsTF;

    /** Button used for closing the server. */
    private JButton closeServerButton;

    /** The reference to the server model. */
    private ChatServer server;

    /** A list of all the online clients. */
    private JList<ServerWorker> clientList;

    /** The color of the enabled start button. */
    private static final Color START_BUTTON_COLOR = new Color(0, 156, 255);

    /** The color of the enabled close button. */
    private static final Color CLOSE_BUTTON_COLOR = new Color(226, 7, 0);

    /** The color of the disabled button. */
    private static final Color DISABLED_BUTTON_COLOR = new Color(91, 91, 91);

    //===========================================================
    //                      Constructor
    //===========================================================

    /**
     * Constructs a new server-view window.
     */
    public ChatServerView() {
        setTitle("Chat Server");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setResizable(false);

        initGUI();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    /**
     * Initializes the graphical user interface.
     */
    private void initGUI() {
        Container pane = getContentPane();
        pane.add(createInfoLabel(), BorderLayout.NORTH);
        pane.add(createCenterPanel(), BorderLayout.CENTER);
        pane.add(createControlPanel(), BorderLayout.SOUTH);
    }

    /**
     * Constructs a new server model and starts the server in a separate thread.
     */
    private void startNewServer() {
        if(portTF.getText().isBlank() || maxClientsTF.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Both port and client limit are required.");
            return;
        }

        try {
            final int port = Integer.parseInt(portTF.getText());
            final int maxClients = Integer.parseInt(maxClientsTF.getText());

            if(port < 0 || port > 65535) {
                JOptionPane.showMessageDialog(this, "Port must be in range from 0 to 65535.");
                return;
            }

            if(maxClients < 1 || maxClients > 255) {
                JOptionPane.showMessageDialog(this, "Client limit must be in range from 1 to 255.");
                return;
            }

            server = new ChatServer(maxClients);
            server.addServerListener(this);
            server.addServerClientListener(this);
            clientList.setModel(new ServerWorkerListModel(server));

            Thread serverThread = new Thread(() -> server.start(port));
            serverThread.setName("Server");
            serverThread.setDaemon(true);
            serverThread.start();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Port and client limit must be positive integers.");
        }
    }

    //===========================================================
    //                 Server listener methods
    //===========================================================

    @Override
    public void serverStarted() {
        String msg = "Starting server on address '" + server.getAddress() + "' and port " + server.getPort() + ".\r\n" +
                     "Client limit set to " + server.getMaxClients() + ".\r\n" +
                     "Waiting for client connections...\r\n\r\n";

        Common.textPaneAppend(logTP, msg, Common.PLAIN_TEXT_COLOR);

        startServerButton.setEnabled(false);
        startServerButton.setBackground(DISABLED_BUTTON_COLOR);
        closeServerButton.setEnabled(true);
        closeServerButton.setBackground(CLOSE_BUTTON_COLOR);

        portTF.setEnabled(false);
        maxClientsTF.setEnabled(false);

        serverStateLabel.setText("Server is running | Address: " + server.getAddress() + " | Port: " + server.getPort());
        updateClientStateLabel();
    }

    @Override
    public void serverClosed() {
        Common.textPaneAppend(logTP, "Server closed.\r\n\r\n", Color.black);
        startServerButton.setEnabled(true);
        startServerButton.setBackground(START_BUTTON_COLOR);
        closeServerButton.setEnabled(false);
        closeServerButton.setBackground(DISABLED_BUTTON_COLOR);

        portTF.setEnabled(true);
        maxClientsTF.setEnabled(true);

        serverStateLabel.setText("Server is closed.");
        clientStateLabel.setText("-");
    }

    @Override
    public void clientConnected(String username) {
        Common.textPaneAppend(logTP, username + " has just connected!\r\n", Common.CLIENT_CONNECTED_COLOR);
        updateClientStateLabel();
    }

    @Override
    public void clientDisconnected(String username) {
        Common.textPaneAppend(logTP, username + " has disconnected from the server.\r\n", Common.CLIENT_DISCONNECTED_COLOR);
        updateClientStateLabel();
    }

    @Override
    public void serverMessage(String message) {
        Common.textPaneAppend(logTP, message + "\r\n", Common.PLAIN_TEXT_COLOR);
    }

    private void updateClientStateLabel() {
        clientStateLabel.setText("Clients online (" + server.getClientCount() + "/" + server.getMaxClients() + ")");
    }

    //===========================================================
    //         Factory methods - panels and log text area
    //===========================================================

    private JLabel createInfoLabel() {
        serverStateLabel = new JLabel("Server is closed.", JLabel.CENTER);
        serverStateLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        serverStateLabel.setBackground(Color.black);
        serverStateLabel.setForeground(Color.white);
        serverStateLabel.setOpaque(true);
        return serverStateLabel;
    }

    private JSplitPane createCenterPanel() {
        logTP = new JTextPane();
        logTP.setEditable(false);
        logTP.setMargin(new Insets(10, 10, 10, 10));

        clientList = new JList<>();
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) clientList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);

        JScrollPane logSP = new JScrollPane(logTP);
        logSP.setBorder(BorderFactory.createEmptyBorder());
        JScrollPane clientSP = new JScrollPane(clientList);
        clientSP.setBorder(BorderFactory.createEmptyBorder());

        JPanel clientPanel = new JPanel(new BorderLayout());
        clientStateLabel = new JLabel("-", JLabel.CENTER);
        clientStateLabel.setForeground(Color.white);
        clientPanel.add(clientStateLabel, BorderLayout.NORTH);
        clientPanel.add(clientSP, BorderLayout.CENTER);
        clientPanel.setBackground(Color.black);
        clientPanel.setBorder(BorderFactory.createEmptyBorder());
        clientPanel.setPreferredSize(new Dimension(0, 0));

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, logSP, clientPanel);
        splitPane.setResizeWeight(0.75);
        splitPane.setDividerSize(1);
        splitPane.setEnabled(false);

        return splitPane;
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(1, 0));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        controlPanel.add(createStartServerButton());

        controlPanel.add(new JLabel("Port:", JLabel.CENTER));
        controlPanel.add(portTF = new JTextField("2468"));
        portTF.setBorder(BorderFactory.createEmptyBorder());
        portTF.setHorizontalAlignment(JTextField.CENTER);
        controlPanel.add(new JLabel("Max clients:", JLabel.CENTER));
        controlPanel.add(maxClientsTF = new JTextField("4"));
        maxClientsTF.setBorder(BorderFactory.createEmptyBorder());
        maxClientsTF.setHorizontalAlignment(JTextField.CENTER);

        controlPanel.add(createCloseServerButton());

        return controlPanel;
    }

    //===========================================================
    //                Factory methods - buttons
    //===========================================================

    private JButton createStartServerButton() {
        startServerButton = new JButton("Start");
        startServerButton.setBackground(START_BUTTON_COLOR);
        startServerButton.addActionListener(l -> startNewServer());
        startServerButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return startServerButton;
    }

    private JButton createCloseServerButton() {
        closeServerButton = new JButton("Close");
        closeServerButton.setBackground(DISABLED_BUTTON_COLOR);
        closeServerButton.addActionListener(l -> server.close());
        closeServerButton.setEnabled(false);
        closeServerButton.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        return closeServerButton;
    }
}