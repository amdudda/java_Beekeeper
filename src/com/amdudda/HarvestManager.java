package com.amdudda;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * Created by amdudda on 11/25/2015.
 */
public class HarvestManager extends JFrame {
    private JPanel rootPanel;
    private JTable harvestTable;
    private JButton quitButton;
    private HarvestTableDataModel htdm;

    public HarvestManager() {
        setContentPane(rootPanel);
        pack();
        setTitle("Beehive Harvest Database Application");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // need to set up our data table
        try {
            String sqlToRun = Queries.getAllHiveData();
            System.out.println(sqlToRun);
            Database.openConnStatement();
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
                System.exit(0);
            }
        });
    }
}
