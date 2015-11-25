package com.amdudda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
    private HarvestTableDataModel htdm;

    public HarvestManager() {
        setContentPane(rootPanel);
        //pack();
        Dimension dim = new Dimension(500, 500);
        setSize(dim);
        setTitle("Beehive Harvest Database Application");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // need to set up our data table
        try {
            String sqlToRun = Queries.getAllHiveData();
            //System.out.println(sqlToRun);
            Database.openConnStatement();
            Statement s = Database.conn.createStatement();
            ResultSet hive = s.executeQuery(sqlToRun);
            htdm = new HarvestTableDataModel(hive);
        } catch (SQLException sqle) {
            System.out.println("Unable to create data model for harvest display table.");
            System.out.println(sqle);
        }

        harvestTable.setModel(htdm);
        // set up the combo box of locations -- but this results in "Operation not allowed after ResultSet closed" message
        // when trying to click in the harvestTable scrollform.
        setupLocationComboBox();
        dateCollectedTextField.setText("YYYY-MM-DD");

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // let's make really truly sure our connections are closed
                Database.closeConnStatement();

                System.exit(0);
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
}
