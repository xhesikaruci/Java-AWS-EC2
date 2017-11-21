package com.amazonaws.samples;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileFromAws {
    public String name = null;
    public String region = null;
    public String parent = null;
    public Integer degree = null;
    public String icon = null;
    public String type = null;
    public boolean isOpen = false;

    //This creator is used to create a bucket
    public FileFromAws(String name, String region) {
        this.name = name;
        this.region = region;
        this.degree = 0;
        this.type = "folder";

        this.icon = "folder.png";

        //add icon

    }

    //creator for file or folder
    public FileFromAws(String name, String parent, Integer degree, String type) {
        this.name = name;
        this.parent = parent;
        this.degree = degree;
        BufferedImage img = null;
        if (type.equals("file")) {
            this.icon = "file.png";
            this.type = "file";

        } else {

            this.icon = "folder.png";
            this.type = "folder";

        }


        //add icon
    }

    @Override
    public String toString() {
        return name;
    }


}
