import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

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
    private JFormattedTextField ftfDOB_DD;
    private MaskFormatter Date;

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

    private void createUIComponents() throws ParseException {
        Date = new MaskFormatter("##");
        Date.setPlaceholderCharacter('#');
        ftfDOB_DD = new JFormattedTextField(Date);
        ftfDOB_DD.setColumns(2);
    }
}
