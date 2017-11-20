package com.amazonaws.samples;

import javax.swing.*;
import java.awt.*;

public class MainFrameGui {

        static Color colors[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE,
                Color.MAGENTA };

        static void add(JTabbedPane tabbedPane, String label) {
            //add the specific parts for ec2 and s3 here
            tabbedPane.addTab(label, null);
        }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tabbed Pane Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        String titles[] = { "EC2", "S3"};
        for (int i = 0, n = titles.length; i < n; i++) {
            add(tabbedPane, titles[i]);
        }

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


}
