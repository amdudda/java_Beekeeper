package com.amdudda;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by amdudda on 11/27/15.
 */
public class ProductivityDataModel extends AbstractTableModel {
    private int rowCount = 0;
    private int colCount = 0;
    private ResultSet rs;

    ProductivityDataModel(ResultSet input_rs) {
        this.rs = input_rs;
        establishMetadata();
    }

    private void establishMetadata() {
        // figure out how many columns of data there are
        try {
            colCount = this.rs.getMetaData().getColumnCount();

        } catch (SQLException se) {
            System.out.println("Error counting columns" + se);
        }
        // and count the number of rows there are
        rowCount = countRows();
    }

    private int countRows() {
        // iterates through the rows and counts them up.
        // I did this independently, though the MovieData code looks almost identical.
        int i = 0;
        try {
            this.rs.beforeFirst();  // move to the beginning of the data set
            while (this.rs.next()) {
                i++;
            }
            this.rs.beforeFirst(); // point back to the start of the data set
        } catch (SQLException sqle) {
            System.out.println("Unable to count rows in database.");
        }
        return i;
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    @Override
    public int getColumnCount() {
        return colCount;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // return the value of a cell in the table
        // copypasta from HarvestTableDataModel
        try {
            //  System.out.println("get value at, row = " +row);
            this.rs.absolute(rowIndex + 1);
            Object o = this.rs.getObject(columnIndex + 1);
            if (o == null) {
                return "0";
            }
            else {
                return o.toString();
            }
        } catch (SQLException se) {
            System.out.println(se);
            //se.printStackTrace();
            return se.toString();
        }
    }

    @Override
    public String getColumnName(int col) {
        // not going to do anything fancy here - just going to output field names
        String colname = "?";
        try {
            colname = this.rs.getMetaData().getColumnName(col+1);
        } catch (SQLException sqle) {
            System.out.println("Unable to fetch column names.\n" + sqle);
        }
        return colname;
    }
}
