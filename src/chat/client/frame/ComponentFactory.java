package chat.client.frame;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

class ComponentFactory {

    /** The font used by this login window's titles. */
    private static final Font TITLE_FONT = new Font("Arial", Font.PLAIN, 12);

    /** The font used by this login window's input fields. */
    private static final Font INPUT_FONT = new Font("Arial", Font.BOLD, 14);

    /** The font used by this login window's login button. */
    private static final Font LOGIN_FONT = new Font("Arial", Font.BOLD, 14);

    private ComponentFactory() {}

    static JTextField createTextField(String title, String initialText) {
        JTextField textField = new JTextField(initialText);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(INPUT_FONT);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(0, 20, 0, 20),
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(Color.lightGray, 2),
                        title,
                        TitledBorder.LEFT,
                        TitledBorder.CENTER,
                        TITLE_FONT,
                        Color.lightGray)
        ));
        return textField;
    }

    static JPanel createButtonArea(String title, Action buttonAction) {
        JButton button = new JButton(title);
        button.setFont(LOGIN_FONT);
        button.setBackground(new Color(0, 156, 255));
        button.setForeground(Color.white);

        button.addActionListener(buttonAction);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.white);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        buttonPanel.add(button, BorderLayout.CENTER);

        return buttonPanel;
    }
}
