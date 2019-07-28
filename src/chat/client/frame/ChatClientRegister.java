package chat.client.frame;

import chat.client.ChatClient;
import chat.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Models the window which allows the client to register a new account.
 */
class ChatClientRegister extends JFrame {

    /** The maximum username length for the user. */
    private static final int MAX_USERNAME_LENGTH = 16;

    /** The minimum password length required. */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /** Field used for inputting the desired username. */
    private JTextField usernameTF;

    /** Field used for inputting the desired password. */
    private JTextField passwordTF;

    /** Field used for inputting the desired server address. */
    private JTextField addressTF;

    /** Field used for inputting the desired server port. */
    private JTextField portTF;

    //===========================================================
    //                        Constructor
    //===========================================================

    /**
     * Constructs a new client-login window.
     */
    ChatClientRegister() {
        setTitle("Chat Register");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 240);
        setResizable(false);

        initGUI();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    private void initGUI() {
        this.setLayout(new GridLayout(0, 1));
        Container pane = getContentPane();

        pane.add(usernameTF = ComponentFactory.createTextField("Username", ""));
        pane.add(passwordTF = ComponentFactory.createTextField("Password", ""));
        pane.add(addressTF = ComponentFactory.createTextField("Address", "localhost"));
        pane.add(portTF = ComponentFactory.createTextField("Port", "2468"));

        Action registerButtonAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                register();
            }
        };

        pane.add(ComponentFactory.createButtonArea("Register", registerButtonAction), BorderLayout.SOUTH);
    }

    private void register() {
        if(!isFormValid()) return;

        ChatClient client = new ChatClient(usernameTF.getText(), passwordTF.getText());
        int registrationStatus  = client.register(addressTF.getText(), Integer.parseInt(portTF.getText()));

        switch (registrationStatus) {
            case Server.REGISTRATION_SUCCEEDED:
                JOptionPane.showMessageDialog(this, "Successfully registered!");
                dispose();
                new ChatClientLogin().setVisible(true);
                break;

            case Server.REGISTRATION_FAILED_USERNAME_ALREADY_TAKEN:
                JOptionPane.showMessageDialog(this, "Username is already taken.");
                break;

            case Server.REGISTRATION_FAILED_IO_ERROR:
                JOptionPane.showMessageDialog(this, "Could not connect to the server.");
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown registration status, registration failed.");
                break;
        }
    }

    /**
     * Checks if the login form was filled in correctly.
     * @return {@code true} if the form was filled in correctly, {@code false} otherwise
     */
    private boolean isFormValid() {
        if(!isUsernameValid()) {
            String msg = "Username must consist only of letters, digits or symbol \"_\"." +
                         "Username cannot be blank.\r\n" +
                         "Username cannot be longer than " + MAX_USERNAME_LENGTH + " symbols.\r\n";
            JOptionPane.showMessageDialog(this, msg);
            return false;
        }

        if(!isPasswordValid()) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 symbols long.");
            return false;
        }

        if(!isAddressValid()) {
            JOptionPane.showMessageDialog(this, "Address is required.");
            return false;
        }

        if(!isPortValid()) {
            JOptionPane.showMessageDialog(this, "Port must be a positive integer value.");
            return false;
        }

        return true;
    }

    private boolean isUsernameValid() {
        String username = usernameTF.getText();

        if(username.isBlank()) return false;
        if(username.length() > MAX_USERNAME_LENGTH) return false;

        for(char c : username.toCharArray()) {
            if(!Character.isLetter(c) && !Character.isDigit(c) && c != '_') {
                return false;
            }
        }

        return true;
    }

    private boolean isPasswordValid() {
        return passwordTF.getText().length() >= MIN_PASSWORD_LENGTH;
    }

    private boolean isAddressValid() {
        return !addressTF.getText().isBlank();
    }

    private boolean isPortValid() {
        try {
            int port = Integer.parseInt(portTF.getText());
            return port >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}