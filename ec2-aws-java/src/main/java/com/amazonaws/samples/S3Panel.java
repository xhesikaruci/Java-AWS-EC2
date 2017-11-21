package com.amazonaws.samples;

import javax.swing.*;
import java.awt.*;

public class S3Panel {
    JSplitPane splitPane ;
    JButton button = new JButton("toto");
    JList<String> list;
    String[] names = {"ehy","you","hey","you","hey","you","hey","you","hey","you","hey","you","hey","you","hey","you","hey","you","hey","you","hey","you",};
    JScrollPane listPane;
    JTextArea textView = new JTextArea();
    JScrollPane textScroll;
    JPanel panel ;


    public S3Panel (){
        textView.setText("well hello there");
        textScroll = new JScrollPane(textView);
        list = new JList<>(names);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setDragEnabled(true);
        listPane = new JScrollPane(list);
        listPane.getViewport().setViewPosition(new Point(20,20));
        listPane.setPreferredSize(new Dimension(80,250));
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPane,textScroll);
        panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        panel.add(splitPane);
        panel.add(button);



    }
}
