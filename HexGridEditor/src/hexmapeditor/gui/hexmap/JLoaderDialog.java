package hexmapeditor.gui.hexmap;

import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;

/* 1.4 example used by DialogDemo.java. */
public class JLoaderDialog extends JDialog implements ActionListener, PropertyChangeListener {

    private String typedText = null;
    private JTextField textField;
//    private DialogDemo dd;
    private String[] protectedName = new String[]{"TEMP", "DEFAULT"};
    private JOptionPane optionPane;
    private String btnString1 = "Enter";
    private String btnString2 = "Cancel";
//    private final boolean isSave;

    /**
     * Returns null if the typed string was invalid;
     * otherwise, returns the string as the user entered it.
     */
    public String getValidatedText() {
        return typedText;
    }

    /**
     * Used when saving.
     */
    public JLoaderDialog(Frame aFrame, String defaultValue) {
        this(aFrame, true, defaultValue);
    }
    
    public JLoaderDialog(Frame aFrame, boolean isSave) {
        this(aFrame, isSave, null);
    }
    /**
     * Creates the reusable dialog.
     */
    public JLoaderDialog(Frame aFrame, boolean isSave, String defaultValue) {
        super(aFrame, true);
//        isSave = isSave;
        textField = new JTextField(10);

        //Create an array of the text and components to be displayed.
        Object[] msg = new Object[2];
        if (isSave) {
            setTitle("Save Map");
            msg[0] = "Name to use to save ?";
            if(defaultValue != null){
                textField.setText(defaultValue);
            }
        } else {
            setTitle("Load Map");
            msg[0] = "Map name to load ?";
        }
        msg[1] = textField;

        //Create an array specifying the number of dialog buttons
        //and their text.
        Object[] options = {btnString1, btnString2};

        //Create the JOptionPane.
        optionPane = new JOptionPane(msg,
                JOptionPane.QUESTION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]);

        //Make this dialog display it.
        setContentPane(optionPane);

        //Handle window closing correctly.
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                /*
                 * Instead of directly closing the window,
                 * we're going to change the JOptionPane's
                 * value property.
                 */
                optionPane.setValue(new Integer(
                        JOptionPane.CLOSED_OPTION));
            }
        });

        //Ensure the text field always gets the first focus.
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent ce) {
                textField.requestFocusInWindow();
            }
        });

        setMinimumSize(new Dimension(350, 150));

        //Register an event handler that puts the text into the option pane.
        textField.addActionListener(this);

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
    }

    /**
     * This method handles events for the text field.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        optionPane.setValue(btnString1);
    }

    /**
     * This method reacts to state changes in the option pane.
     */
    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();

        if (isVisible()
                && (e.getSource() == optionPane)
                && (JOptionPane.VALUE_PROPERTY.equals(prop)
                || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
            Object value = optionPane.getValue();

            if (value == JOptionPane.UNINITIALIZED_VALUE) {
                //ignore reset
                return;
            }

            //Reset the JOptionPane's value.
            //If you don't do this, then if the user
            //presses the same button next time, no
            //property change event will be fired.
            optionPane.setValue(
                    JOptionPane.UNINITIALIZED_VALUE);

            if (btnString1.equals(value)) {
                typedText = textField.getText();
                String ucText = typedText.toUpperCase();
                //@todo check if the map already exist and if override
                //@todo check if the map exist before loading
                if (!protectedName[0].equals(ucText) && !protectedName[1].equals(ucText)) {
                    //we're done; clear and dismiss the dialog
                    clearAndHide();
                } else {
                    //text was invalid
                    textField.selectAll();
                    JOptionPane.showMessageDialog(
                            JLoaderDialog.this,
                            "Sorry, \"" + typedText + "\" "
                            + "isn't a valid name.\n",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
                    typedText = null;
                    textField.requestFocusInWindow();
                }
            } else { //user closed dialog or clicked cancel;
                typedText = null;
                clearAndHide();
            }
        }
    }

    /**
     * This method clears the dialog and hides it.
     */
    public void clearAndHide() {
        textField.setText(null);
        setVisible(false);
    }
}