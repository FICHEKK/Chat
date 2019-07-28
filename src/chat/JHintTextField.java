package chat;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A slightly modified version of the {@link JTextField} that additionally
 * offers a simple textual hint before any input has been made.
 */
public class JHintTextField extends JTextField implements FocusListener {

    /** The textual hint. */
    private String hint;

    /** Flag indicating whether the hint is currently being shown. */
    private boolean showingHint;

    /**
     * Constructs a new {@link JHintTextField} with the specified hint.
     * @param hint the hint to be displayed
     */
    public JHintTextField(String hint) {
        super(hint);
        super.addFocusListener(this);
        this.hint = hint;
        this.showingHint = true;
    }

    @Override
    public void focusGained(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText("");
            showingHint = false;
        }
    }
    @Override
    public void focusLost(FocusEvent e) {
        if(this.getText().isEmpty()) {
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }
}