package chat.client.frame;

import chat.client.ChatClient;
import chat.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Models the window which allows the client to log-in to the
 * server.
 */
public class ChatClientLogin extends JFrame {

    /** Field used for inputting the username. */
    private JTextField usernameTF;

    /** Field used for inputting the username. */
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
    public ChatClientLogin() {
        setTitle("Chat Login");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 300);
        setResizable(false);

        initGUI();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
    }

    private void initGUI() {
        this.setLayout(new GridLayout(0, 1));
        Container pane = getContentPane();

        pane.add(usernameTF = ComponentFactory.createTextField("Username", "a"));
        pane.add(passwordTF = ComponentFactory.createTextField("Password", "kosarkas"));
        pane.add(addressTF = ComponentFactory.createTextField("Address", "localhost"));
        pane.add(portTF = ComponentFactory.createTextField("Port", "2468"));

        Action loginButtonAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        };
        Action registrationButtonAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registration();
            }
        };

        pane.add(ComponentFactory.createButtonArea("Login", loginButtonAction));
        pane.add(ComponentFactory.createButtonArea("Registration", registrationButtonAction));
    }

    /**
     * Performs the client log-in action.
     */
    private void login() {
        if(!isFormValid()) return;

        ChatClient client = new ChatClient(usernameTF.getText(), passwordTF.getText());
        int loginStatus  = client.login(addressTF.getText(), Integer.parseInt(portTF.getText()));

        switch (loginStatus) {
            case Server.LOGIN_ACCEPTED:
                dispose();
                new ChatClientView(client).setVisible(true);
                break;

            case Server.LOGIN_DENIED_SERVER_FULL:
                JOptionPane.showMessageDialog(this, "Server is full.");
                break;

            case Server.LOGIN_DENIED_CLIENT_ALREADY_LOGGED_IN:
                JOptionPane.showMessageDialog(this, "You are already logged in.");
                break;

            case Server.LOGIN_DENIED_IO_ERROR:
                JOptionPane.showMessageDialog(this, "Error establishing the connection with the server.");
                break;

            case Server.LOGIN_DENIED_CLIENT_IS_BANNED:
                JOptionPane.showMessageDialog(this, "You are banned from the server.");
                break;

            case Server.LOGIN_DENIED_USERNAME_NOT_REGISTERED:
                JOptionPane.showMessageDialog(this, "Given username is not registered.");
                break;

            case Server.LOGIN_DENIED_WRONG_PASSWORD:
                JOptionPane.showMessageDialog(this, "Wrong password.");
                break;

            default:
                JOptionPane.showMessageDialog(this, "Unknown login status, could not join.");
                break;
        }
    }

    /**
     * Opens the client registration window.
     */
    private void registration() {
        dispose();
        new ChatClientRegister().setVisible(true);
    }

    /**
     * Checks if the login form was filled in correctly.
     * @return {@code true} if the form was filled in correctly, {@code false} otherwise
     */
    private boolean isFormValid() {
        if(!isUsernameValid() || !isPasswordValid() || !isAddressValid() || !isPortValid()) {
            JOptionPane.showMessageDialog(this, "All fields is required.");
            return false;
        }

        return true;
    }

    private boolean isUsernameValid() {
        return !usernameTF.getText().isBlank();
    }

    private boolean isPasswordValid() {
        return !passwordTF.getText().isBlank();
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