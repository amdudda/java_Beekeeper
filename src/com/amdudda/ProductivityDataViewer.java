package com.amdudda;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by amdudda on 11/27/15.
 */
public class ProductivityDataViewer extends JFrame {
    private JTable productivityDataTable;
    private JScrollPane productivityDataScrollPane;
    private JButton returnToMainScreenButton;
    private JPanel dataPanel;
    private ProductivityDataModel pdm;

    public ProductivityDataViewer() {
        setContentPane(dataPanel);
        pack();
        setTitle("Productivity Data Viewer");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // need to set up our data table
        try {
            String sqlToRun = Queries.getCurrentVsPreviousYearProduction();
            ResultSet dataView = Database.statement.executeQuery(sqlToRun);
            pdm = new ProductivityDataModel(dataView);
        } catch (SQLException sqle) {
            System.out.println("Unable to create data model for productivity data table.\n" + sqle);
        }

        // close the window when done
        returnToMainScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
