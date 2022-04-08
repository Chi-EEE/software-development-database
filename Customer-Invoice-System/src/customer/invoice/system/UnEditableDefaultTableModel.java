/**
 * Author: Chi Huu Huynh
 * Login: C00261172
 * Date: 08/04/2022
 * Summary: Used for uneditable tables
 */
package customer.invoice.system;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author C00261172
 */
public class UnEditableDefaultTableModel extends DefaultTableModel {

    public UnEditableDefaultTableModel() {
    }

    public UnEditableDefaultTableModel(int rowCount, int columnCount) {
        super(rowCount, columnCount);
    }

    public UnEditableDefaultTableModel(Vector<?> columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public UnEditableDefaultTableModel(Object[] columnNames, int rowCount) {
        super(columnNames, rowCount);
    }

    public UnEditableDefaultTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
        super(data, columnNames);
    }

    public UnEditableDefaultTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;//This causes all cells to be not editable
    }
}
