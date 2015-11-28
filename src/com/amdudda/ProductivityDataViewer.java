package com.amdudda;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by amdudda on 11/27/15.
 */
public class ProductivityDataViewer extends JFrame {
    private JTable productivityDataTable;
    private JButton returnToMainScreenButton;
    private JPanel dataPanel;
    private ProductivityDataModel pdm;
    private ResultSet dataView;
    private Statement dataStatement = null;

    public ProductivityDataViewer() {
        setContentPane(dataPanel);
        pack();
        setTitle("Productivity Data Viewer");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // need to set up our data table
        try {
            String sqlToRun = Queries.getCurrentVsPreviousYearProduction();
            // System.out.println(sqlToRun);
            dataStatement = Database.conn.createStatement();
            dataView = dataStatement.executeQuery(sqlToRun);
            pdm = new ProductivityDataModel(dataView);
        } catch (SQLException sqle) {
            System.out.println("Unable to create data model for productivity data table.\n" + sqle);
        }
        // and plonk the data into the table.
        productivityDataTable.setModel(pdm);

        // close the window when done
        returnToMainScreenButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    dataView.close();
                    dataStatement.close();
                } catch (SQLException sqle) {
                    System.out.println("Unable to close dataview ResultSet.\n" + sqle);
                }
                dispose();
            }
        });
    }
}
