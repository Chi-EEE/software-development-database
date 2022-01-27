import javax.swing.*;

// Verify if the value in JSpinner is a number after submit
public class AddProduct extends JFrame{
    private String[] products = {"Toy"};

    private JPanel mainPanel;
    private JButton submitButton;
    private JComboBox productBox;
    private JSpinner spinner1;

    public AddProduct() {
        setContentPane(mainPanel);
        setTitle("Create Invoice");
        setSize(400, 140);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setVisible(true);
    }

    private void createUIComponents() {
        productBox = new JComboBox(products);
    }
}
