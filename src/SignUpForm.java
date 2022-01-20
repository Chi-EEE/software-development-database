import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SignUpForm  extends JFrame{
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JButton okButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JLabel lbWelcome;
    private JTextField tfDOB_YY;
    private JTextField tfDOB_MM;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JTextField textField1;
    private JLabel tfEmail_Confirm;
    private JTextField textField2;
    private JTextField tfDOB_DD;
    private JFormattedTextField ftfDOB_DD;
    private MaskFormatter Date;

    public SignUpForm() {
        setContentPane(mainPanel);
        setTitle("Welcome");
        setSize(440, 390);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(440, 390));
        setVisible(true);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String firstName = tfFirstName.getText();
                String lastName = tfLastName.getText();
                lbWelcome.setText("Welcome " + firstName + " " + lastName);
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tfFirstName.setText("");
                tfLastName.setText("");
                lbWelcome.setText("");
            }
        });
    }

    public static void main(String[] args) {
        SignUpForm signUpForm = new SignUpForm();
    }

    private void setDocumentFilter(JTextField tf) {
        PlainDocument doc = (PlainDocument) tf.getDocument();
        doc.setDocumentFilter(new MyIntFilter());
    }

    private void createUIComponents() throws ParseException {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        tfDOB_DD = new JFormattedTextField(df);

//        tfDOB_DD.addKeyListener(new KeyAdapter() {
//            public void keyTyped(KeyEvent e) {
//                char c = e.getKeyChar();
//                if (!((c >= '0') && (c <= '9') ||
//                        (c == KeyEvent.VK_BACK_SPACE) ||
//                        (c == KeyEvent.VK_DELETE) || (c == KeyEvent.VK_SLASH)))
//                {
//                    JOptionPane.showMessageDialog(null, "Please Enter Valid");
//                    e.consume();
//                }
//            }
//        });
//        tfDOB_DD = new JTextField(1);
//        tfDOB_MM = new JTextField(1);
//        tfDOB_YY = new JTextField(1);
//        setDocumentFilter(tfDOB_DD);
//        setDocumentFilter(tfDOB_MM);
//        setDocumentFilter(tfDOB_YY);
    }

    /**
     * Credits: https://stackoverflow.com/a/11093360
     */
    class MyIntFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string,
                                 AttributeSet attr) throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.insert(offset, string);

            if (test(sb.toString())) {
                super.insertString(fb, offset, string, attr);
            } else {
                // warn the user and don't allow the insert
            }
        }

        private boolean test(String text) {
            try {
                Integer.parseInt(text);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text,
                            AttributeSet attrs) throws BadLocationException {

            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.replace(offset, offset + length, text);

            if (test(sb.toString())) {
                super.replace(fb, offset, length, text, attrs);
            } else {
                // warn the user and don't allow the insert
            }

        }

        @Override
        public void remove(FilterBypass fb, int offset, int length)
                throws BadLocationException {
            Document doc = fb.getDocument();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.getText(0, doc.getLength()));
            sb.delete(offset, offset + length);

            if (test(sb.toString())) {
                super.remove(fb, offset, length);
            } else {
                // warn the user and don't allow the insert
            }

        }
    }
}
