package com.amdudda;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;

/**
 * Created by amdudda on 11/25/2015.
 */
public class HarvestManager extends JFrame {
    private JPanel rootPanel;
    private JTable harvestTable;
    private JButton quitButton;
    private JScrollPane harvestTableScrollPane;
    private JTextField dateCollectedTextField;
    private JLabel dateCollectedLabel;
    private JTextField weightTextField;
    private JComboBox hiveLocationComboBox;
    private JButton addHarvestInfoButton;
    private JButton deleteSelectedRecordButton;
    private JButton totalHoneyCollectedForButton;
    private JTextArea yearTextArea;
    private HarvestTableDataModel htdm;

    public HarvestManager() {
        setContentPane(rootPanel);
        //pack();
        Dimension dim = new Dimension(500, 500);
        setSize(dim);
        setTitle("Beehive Harvest Database Application");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // make sure our connection is up and running.
        Database.openConnStatement();

        // set up the combo box of locations -- but this results in "Operation not allowed after ResultSet closed" message
        // when trying to click in the harvestTable scrollform.
        setupLocationComboBox();
        dateCollectedTextField.setText("YYYY-MM-DD");

        // need to set up our data table
        try {
            String sqlToRun = Queries.getAllHiveData();
            //System.out.println(sqlToRun);
            /*Statement s = Database.conn.createStatement();
            ResultSet hive = s.executeQuery(sqlToRun);*/
            Database.rs = Database.statement.executeQuery(sqlToRun);
            htdm = new HarvestTableDataModel(Database.rs);
        } catch (SQLException sqle) {
            System.out.println("Unable to create data model for harvest display table.");
            System.out.println(sqle);
        }

        harvestTable.setModel(htdm);


        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // let's make really truly sure our connections are closed
                Database.closeConnStatement();

                System.exit(0);
            }
        });

        addHarvestInfoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // this inserts the data into the database.
                // TODO: input validation
                String dc = dateCollectedTextField.getText();
                Double wt = Double.parseDouble(weightTextField.getText());
                int hn = hiveLocationComboBox.getSelectedIndex() +1;
                System.out.println(hn);
                Database.addHoneyData(dc,wt,hn);  // this doesn't look parameterized here, but look deep enough and you'll find that it is.
                try {
                    Database.rs = Database.statement.executeQuery(Queries.getAllHiveData());
                } catch (SQLException sqle) {
                    System.out.println("Unable to get updated database info.");
                    System.out.println(sqle);
                }
                // refresh the datamodel
                htdm.refresh(Database.rs);
                // and reset the data entry boxes:
                dateCollectedTextField.setText("YYYY-MM-DD");
                weightTextField.setText("");
            }
        });

        deleteSelectedRecordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // deletes the selected record in the database
                int pk_to_delete = -1;
                try {
                    int row = harvestTable.getSelectedRow();
                    // System.out.println("selected record = " + row);
                    // get the column number of the primary key column - not guaranteed to be the zeroth column
                    int col = -1;
                    for (int i = 0; i < htdm.getColumnCount(); i++) {
                        if (Database.rs.getMetaData().getColumnName(i+1).equals(Database.PK_COLUMN)) {
                            col = i;
                            break;
                        }
                    }
                    pk_to_delete = Integer.parseInt(htdm.getValueAt(row, col).toString());
                } catch (SQLException sqle) {
                    System.out.println("Unable to get primary key of record to delete.");
                    System.out.println(sqle);
                }

                try {
                    // construct the record deletion query; data is clean because it's extracted from our database
                    // and record id is not an editable field in the table.
                    String sqlToRun = "DELETE FROM " + Database.HONEY_TABLE_NAME + " WHERE " +
                            Database.PK_COLUMN + " = " + pk_to_delete;
                    Database.statement.executeUpdate(sqlToRun);
                    // and update the resultset with the new info.
                    Database.rs = Database.statement.executeQuery(Queries.getAllHiveData());
                } catch (SQLException sqle) {
                    System.out.println("Unable to delete record from database.\n" + sqle);
                }

                htdm.refresh(Database.rs);
            }
        });


        dateCollectedTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                boolean doAlert = !(validDate(dateCollectedTextField.getText()) ||
                        dateCollectedTextField.getText().equals("YYYY-MM-DD"));
                if (doAlert) {
                    // if it's not valid for the field, need to tell hte user and set the focus back on hte field.
                    JOptionPane.showMessageDialog(rootPanel, "Please enter a valid date in YYYY-MM-DD format.");
                    dateCollectedTextField.grabFocus();
                }
            }
        });
        weightTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                if (!validWeight(weightTextField.getText())) {
                    // if it's not valid for the field, need to tell hte user and set the focus back on hte field.
                    JOptionPane.showMessageDialog(rootPanel, "Please enter a weight less than 1000kg and \nwith no more than 2 decimal places.");
                    weightTextField.grabFocus();
                }

            }
        });
        totalHoneyCollectedForButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // generates a popup with total honey collected in year:
                try {
                    PreparedStatement ps = Database.conn.prepareStatement(Queries.getTotalWeightOfAllHoneyForYear());
                    ps.setInt(1,Integer.parseInt(yearTextArea.getText()));
                    ResultSet tempData = ps.executeQuery();
                    tempData.next();
                    double totalWt = tempData.getDouble(1);
                    JOptionPane.showMessageDialog(rootPanel,String.format("A total of %.2fkg of honey were harvested in %s", totalWt, yearTextArea.getText()));
                    tempData.close();
                } catch (SQLException sqle) {
                    System.out.println(sqle);
                }
            }
        });
    }

    private void setupLocationComboBox() {
        try {
            Database.rs = Database.statement.executeQuery(Queries.getHiveLocations());
            while (Database.rs.next()) {
                hiveLocationComboBox.addItem(Database.rs.getString(1));
            }
        } catch (SQLException sqle) {
            System.out.println("Unable to get list of locations");
            System.out.println(sqle);
        }
    }

    private boolean validDate(String date) {
        try {
            // HarvestTableDM won't share with this, so welcome to Land Of Copypasta!
            String[] dateparts = date.split("-");
            if (dateparts.length != 3) return false;
            int year = Integer.parseInt(dateparts[0]);
            int month = Integer.parseInt(dateparts[1]);
            int day = Integer.parseInt(dateparts[2]);
            return ( year >= 1995 && year <= Integer.parseInt(Year.now().toString())) &&
                    (month > 0 && month < 13) &&
                    (day > 0 && day <=31);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean validWeight(String weight) {
        // how to split string at a dot: http://javadevnotes.com/java-string-split-dot-examples
        boolean verdict;
        try {
            System.out.println(weight);
            String[] wtsplit = weight.split("\\.");
            if (wtsplit.length == 2) {
                verdict = (wtsplit[1].length() < 3 && Integer.parseInt(wtsplit[0]) < 1000);
            } else {
                verdict = (Integer.parseInt(wtsplit[0]) < 1000);
            }
        } catch (Exception e) {
            //System.out.println(e);
            verdict = false;
        }

        // System.out.println(verdict);
        return verdict;
    }
}
