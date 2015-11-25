package com.amdudda;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by amdudda on 11/25/2015.
 */
public class HarvestManager extends JFrame {
    private JPanel rootPanel;
    private JTable harvestTable;
    private JButton quitButton;

    public HarvestManager() {
        setContentPane(rootPanel);
        pack();
        setTitle("Beehive Harvest Database Application");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        quitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }
}
