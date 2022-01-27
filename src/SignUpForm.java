import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignUpForm extends JFrame {
    private JTextField tfFirstName;
    private JTextField tfLastName;
    private JButton okButton;
    private JButton clearButton;
    private JPanel mainPanel;
    private JLabel lbWelcome;
    private JTextField tfDOB_YY;
    private JTextField tfDOB_DD;
    private JTextField tfPhone;
    private JTextField tfEmail;
    private JTextField textField1;
    private JLabel tfEmail_Confirm;
    private JTextField textField2;
    private JSpinner DOB_MM;

    public SignUpForm() {
        setContentPane(mainPanel);
        setTitle("Sign Up");
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
        CreateInvoice invoice = new CreateInvoice();
        AddProduct add = new AddProduct();
    }

    private void createUIComponents() {
        tfDOB_DD = new JTextField();

        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        SpinnerListModel monthModel = new SpinnerListModel(monthNames);
        DOB_MM = new JSpinner(monthModel);

        tfDOB_YY = new JTextField();

        PlainDocument doc = (PlainDocument) tfDOB_DD.getDocument();
        doc.setDocumentFilter(new IntFilter());

        doc = (PlainDocument) tfDOB_YY.getDocument();
        doc.setDocumentFilter(new IntFilter());
    }
}
