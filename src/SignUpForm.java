import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpForm  extends JFrame{
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JButton okButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JLabel lbWelcome;
    private JTextField tfDOB_DD;
    private JTextField tfDOB_YY;
    private JTextField tfDOB_MM;
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
}
