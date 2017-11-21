package com.amazonaws.samples;

import javax.swing.*;
import java.awt.*;

public class MainFrameGui {

        static Color colors[] = { Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE,
                Color.MAGENTA };
        static S3Panel s3Panel = new S3Panel();

        static void add(JTabbedPane tabbedPane, String label,Component component) {
            //add the specific parts for ec2 and s3 here
            tabbedPane.addTab(label, component);
        }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tabbed Pane Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500,500);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        add(tabbedPane, "EC2",new Button("otot"));
        add(tabbedPane, "S3",s3Panel.panel);


        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }


}
