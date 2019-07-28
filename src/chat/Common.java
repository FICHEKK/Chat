package chat;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;

/**
 * Offers functionality required by both the server side and
 * the client side of the chat application.
 */
public class Common {

    /** The color of the console's plain text. */
    public static final Color PLAIN_TEXT_COLOR = Color.black;

    /** The color of the text that displays the new client connection. */
    public static final Color CLIENT_CONNECTED_COLOR = Color.blue;

    /** The color of the text that displays the client disconnecting message. */
    public static final Color CLIENT_DISCONNECTED_COLOR = Color.red;



    /** The color of the text that displays the "client kicked" message. */
    public static final Color CLIENT_KICKED_COLOR = Color.red;

    /** The color of the text that displays the "client banned" message. */
    public static final Color CLIENT_BANNED_COLOR = Color.magenta;

    /** The color of the text that displays the "client deleted" message. */
    public static final Color CLIENT_DELETED_COLOR = new Color(0x704F00);



    /** The color of the text that displays the private client message. */
    public static final Color PRIVATE_CLIENT_MESSAGE_COLOR = new Color(0x05B500);

    /** The color of the text that displays the private server message. */
    public static final Color PRIVATE_SERVER_MESSAGE_COLOR = Color.orange;

    /** The color of the text that displays the global client message. */
    public static final Color GLOBAL_CLIENT_MESSAGE_COLOR = Color.black;

    /** The color of the text that displays the global server message. */
    public static final Color GLOBAL_SERVER_MESSAGE_COLOR = Color.red;

    /**
     * Appends the given message to the specified {@link JTextPane}.
     * @param textPane the text pane to be edited
     * @param message the message to be displayed
     * @param color the color of the message
     */
    public static void textPaneAppend(JTextPane textPane, String message, Color color) {
        textPaneAppend(textPane, message, color, false, false);
    }

    /**
     * Appends the given message to the specified {@link JTextPane}.
     * @param textPane the text pane to be edited
     * @param message the message to be displayed
     * @param color the color of the message
     * @param bold if the text should appear <b>bolded</b>
     * @param italic if the text should appear <i>italic</i>
     */
    public static void textPaneAppend(JTextPane textPane, String message, Color color, boolean bold, boolean italic) {
        textPane.setEditable(true);

        StyleContext sc = StyleContext.getDefaultStyleContext();

        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, color);
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        aset = sc.addAttribute(aset, StyleConstants.CharacterConstants.Bold, bold);
        aset = sc.addAttribute(aset, StyleConstants.CharacterConstants.Italic, italic);

        int len = textPane.getDocument().getLength();
        textPane.setCaretPosition(len);
        textPane.setCharacterAttributes(aset, false);
        textPane.replaceSelection(message);

        textPane.setEditable(false);
    }
}