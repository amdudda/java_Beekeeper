package com.amdudda;

import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by amdudda on 11/25/2015.
 * Adapted from MovieDataModel used in previous class.
 */
public class HarvestTableDataModel extends AbstractTableModel {
    ResultSet rs;
    private int rowcount = 0;
    private int colcount = 0;

    HarvestTableDataModel(ResultSet resultSet) {
        this.rs = resultSet;
        establishMetadata();
    }

    private void establishMetadata() {
        // figure out how many columns of data there are
        try {
            colcount = this.rs.getMetaData().getColumnCount();

        } catch (SQLException se) {
            System.out.println("Error counting columns" + se);
        }
        // and count the number of rows there are
        rowcount = countRows();
    }

    protected void refresh(){
        // a public version allowing the code to update the object's metadata when
        // new data is added.
        establishMetadata();
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
        return rowcount;
    }

    @Override
    public int getColumnCount() {
        return colcount;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        // return the value of a cell in the table
        // copypasta from MovieDataModel - I sort of understand it but
        // don't quite grok it - why does this return an Object and not a String?
        try{
            //  System.out.println("get value at, row = " +row);
            rs.absolute(rowIndex+1);
            Object o = rs.getObject(columnIndex+1);
            return o.toString();
        }catch (SQLException se) {
            System.out.println(se);
            //se.printStackTrace();
            return se.toString();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col){
        try {
            // The only non-editable column is the Primary Key column & location name.
            boolean notPK = !(this.rs.getMetaData().getColumnLabel(col+1).equals(Database.PK_COLUMN));
            boolean notHiveName = !(this.rs.getMetaData().getColumnLabel(col+1).equals(Database.LOCATION_COLUMN));
            return (notPK && notHiveName);
        } catch (SQLException sqle) {
            System.out.println("Oops, unable to get column metadata.");
            return false;
        }
    }

    @Override
    public String getColumnName(int col){
        //Get from ResultSet metadata, which contains the database column names
        // This is the code from MovieDataModel that actuall puts column names in the top of the table.
        try {
            //System.out.println("colname = " + this.rs.getMetaData().getColumnName(col + 1));
            return this.rs.getMetaData().getColumnName(col + 1);
        } catch (SQLException se) {
            System.out.println("Error fetching column names" + se);
            return "?";
        }
    }
}
