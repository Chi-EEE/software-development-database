import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class CreateInvoice extends JFrame {
    private JPanel mainPanel;
    private JButton addProductButton;
    private JScrollPane ProductTable;
    private JButton sendInvoiceButton;

    public CreateInvoice() {
        setContentPane(mainPanel);
        setTitle("Create Invoice");
        setSize(440, 390);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(440, 390));
        setVisible(true);
    }

    private void createUIComponents() {
        // Information for Table
        String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
                "Vegetarian"};
        Object[][] data = {
                {"Kathy", "Smith",
                        "Snowboarding", new Integer(5), new Boolean(false)},
                {"John", "Doe",
                        "Rowing", new Integer(3), new Boolean(true)},
                {"Sue", "Black",
                        "Knitting", new Integer(2), new Boolean(false)},
                {"Jane", "White",
                        "Speed reading", new Integer(20), new Boolean(true)},
                {"Joe", "Brown",
                        "Pool", new Integer(10), new Boolean(false)}
        };

        JTable table = new JTable(data, columnNames);

        //instance table model
        DefaultTableModel tableModel = new DefaultTableModel(data, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return column == 3;
            }
        };

        table.setModel(tableModel);

        ProductTable = new JScrollPane(table);
        table.setFillsViewportHeight(true); // Sets the JTable size to always fit the scroll pane

    }

}
