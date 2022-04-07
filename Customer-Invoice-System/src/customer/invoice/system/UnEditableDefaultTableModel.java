/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
