import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class SignUpForm extends JFrame {
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JButton okButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JLabel lbWelcome;
    private JTextField tfDOB_YY;
    private JTextField tfDOB_MM;
    private JTextField tfDOB_DD;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JTextField textField1;
    private JLabel tfEmail_Confirm;
    private JTextField textField2;

    public SignUpForm() {
        setContentPane(mainPanel);
        setTitle("Welcome");
        setSize(450, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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

    private void createUIComponents() {
        tfDOB_DD = new JTextField();
        tfDOB_MM = new JTextField();
        tfDOB_YY = new JTextField();

        PlainDocument doc = (PlainDocument) tfDOB_DD.getDocument();
        doc.setDocumentFilter(new MyIntFilter());

        doc = (PlainDocument) tfDOB_MM.getDocument();
        doc.setDocumentFilter(new MyIntFilter());

        doc = (PlainDocument) tfDOB_YY.getDocument();
        doc.setDocumentFilter(new MyIntFilter());
    }

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

            if (sb.toString().length() == 0) {
                super.replace(fb, offset, length, "", null);
            } else {
                if (test(sb.toString())) {
                    super.remove(fb, offset, length);
                }
                else {                // warn the user and don't allow the insert

                }
            }
        }
    }
}
