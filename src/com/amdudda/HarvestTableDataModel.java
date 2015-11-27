package com.amdudda;

import javax.swing.table.AbstractTableModel;
import javax.xml.crypto.Data;
import java.sql.Date;
import java.sql.PreparedStatement;
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
        // System.out.println(colcount + " columns x " + rowcount + " rows");
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

    protected void refresh(ResultSet newRS) {
        // a public version allowing the code to update the object's metadata when
        // new data is added.
        this.rs = newRS;
        establishMetadata();
        this.fireTableDataChanged();
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
        try {
            //  System.out.println("get value at, row = " +row);
            rs.absolute(rowIndex + 1);
            Object o = rs.getObject(columnIndex + 1);
            return o.toString();
        } catch (SQLException se) {
            System.out.println(se);
            //se.printStackTrace();
            return se.toString();
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        try {
            // The only non-editable column is the Primary Key column & location name.
            boolean notPK = !(this.rs.getMetaData().getColumnLabel(col + 1).equals(Database.PK_COLUMN));
            boolean notHiveName = !(this.rs.getMetaData().getColumnLabel(col + 1).equals(Database.LOCATION_COLUMN));
            return (notPK && notHiveName);
        } catch (SQLException sqle) {
            System.out.println("Oops, unable to get column metadata.");
            return false;
        }
    }

    @Override
    public String getColumnName(int col) {
        //Get from ResultSet metadata, which contains the database column names
        // This is the code from MovieDataModel that actually puts column names in the top of the table.
        try {
            String header = this.rs.getMetaData().getColumnName(col + 1);
            if (header.equals(Database.PK_COLUMN)) return "RecordID";
            else if (header.equals(Database.DATE_COLLECTED_COLUMN)) return "Date Collected";
            else if (header.equals(Database.WEIGHT_COLUMN)) return "Weight (in kg)";
            else if (header.equals(Database.LOCATION_COLUMN)) return "Location";
            else return header;
        } catch (SQLException se) {
            System.out.println("Error fetching column names" + se);
            return "?";
        }
    }

    // some additional methods for data management:
    public void insertRecord(String dateColl, double wt, int locID) {
        // lets user add record: create a new
        Database.addHoneyData(dateColl, wt, locID);
        try {
            Database.rs = Database.statement.executeQuery(Queries.getAllHiveData());
        } catch (SQLException sqle) {
            System.out.println("Unable to update Harvest Data Table.");
        }
    }

    @Override
    //This is called when user edits an editable cell
    public void setValueAt(Object newValue, int row, int col) {
        String updatedValue = newValue.toString();
        // for now, assume column 0 is the PK
        int pk = Integer.parseInt(getValueAt(row,0).toString());
        PreparedStatement psUpdate = null;

        try {
            String sqlToRun = "UPDATE " + Database.HONEY_TABLE_NAME + " " +
                    "SET " + Database.rs.getMetaData().getColumnName(col+1) + " = ? " +
                    "WHERE " + Database.PK_COLUMN + " = ?";
            psUpdate = Database.conn.prepareStatement(sqlToRun);

            // need to do different things based on which column entered
            if (getColumnName(col).equals("Date Collected")) {
                // TODO: validate the date format
                psUpdate.setDate(1,Date.valueOf(updatedValue));
                psUpdate.setInt(2,pk);
            } else if (getColumnName(col).equals("Weight (in kg)")) {
                // validate the weight as a decimal(3,2) valued double.
                // TODO: if not valid, alert the user.
                psUpdate.setDouble(1,Double.parseDouble(updatedValue));
                psUpdate.setInt(2,pk);
            }

            // and update the table & query results
            psUpdate.executeUpdate();
            psUpdate.close();
            Database.rs = Database.statement.executeQuery(Queries.getAllHiveData());

        } catch (SQLException sqle) {
            System.out.println("Unable to update record:\n" + sqle);
        }

        // refresh the table
        refresh(Database.rs);
        fireTableDataChanged();



    }
}
