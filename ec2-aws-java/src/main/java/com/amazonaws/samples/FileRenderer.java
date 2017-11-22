package com.amazonaws.samples;

import javax.swing.*;
import java.awt.*;

public class FileRenderer extends JLabel implements ListCellRenderer<FileFromAws> {


    public FileRenderer(){
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends FileFromAws> jList, FileFromAws fileFromAws,
                                                  int index, boolean isSelected, boolean hasFocus) {
        //ImageIcon icon = fileFromAws.icon;
        //setIcon(icon);
        String stringToDisplay = "";
        for (int i = 0; i < fileFromAws.degree; i++)
            stringToDisplay += "&emsp;&emsp;&emsp;&emsp;"; //this adds 4 spaces before the icon in html.



        setText("<html>"+stringToDisplay+" <img src=\"file:"+fileFromAws.icon+"\">&emsp;"+
                fileFromAws.name.split("/")[fileFromAws.name.split("/").length-1]+"</html>");



        if (isSelected) {
            setBackground(jList.getSelectionBackground());
            setForeground(jList.getSelectionForeground());
        } else {
            if (fileFromAws.type.equals("file")){
                setBackground(Color.WHITE);
            }else{
                setBackground(Color.cyan);
            }
            setForeground(jList.getForeground());
        }




        return this;
    }
}
