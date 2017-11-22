package com.amazonaws.samples;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class MouseListener extends MouseAdapter {

    public void mouseClicked(MouseEvent e) {
        JList theList = (JList) e.getSource();
        if (e.getClickCount() == 2) {
            int index = theList.locationToIndex(e.getPoint());
            if (index >= 0) {
                Object o = theList.getModel().getElementAt(index);
                System.out.println("Double-clicked on: " + o.toString());
            }
        }
    }
}
