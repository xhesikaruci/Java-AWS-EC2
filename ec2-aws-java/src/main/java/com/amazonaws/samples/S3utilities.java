package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.util.IOUtils;
import org.omg.IOP.Encoding;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

//this class regroups all the utilities for S3, such as list object, listing bucket, get file, put file
public class S3utilities {
    static JPanel getInformationAboutBucket(FileFromAws bucket) {
        int size = 0;
        int numberOfFiles = 0;
        JPanel panel = new JPanel();
        java.util.List<S3ObjectSummary> summaries;

        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "something went wrong with your credentials",
                    e);
        }
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(getBucketLocation(bucket.name))
                .build();

        if (S3Panel.infos.get(bucket.name) == null) {

            System.out.println("from web");
            ObjectListing listing = s3.listObjects(bucket.name);
            summaries = listing.getObjectSummaries();
            S3Panel.infos.put(bucket.name,summaries);

        } else {
            System.out.println("from cache");
            summaries = S3Panel.infos.get(bucket.name);
        }

        for (S3ObjectSummary s : summaries
                ) {
            System.out.println(s);
            size += s.getSize();
            numberOfFiles++;
        }
        panel.add(new JLabel("Size: " + size));
        panel.add(new JLabel("number of objects: " + numberOfFiles));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JButton openButton = new JButton("Open this bucket");
        JButton deleteButton = new JButton("Delete this bucket");
        JButton uploadButton = new JButton("Upload files to this bucket");
        JButton closeButton = new JButton("Close the bucket");

        openButton.addActionListener(actionEvent -> {
            displayChildren(bucket,summaries);

        });

        deleteButton.addActionListener(actionEvent -> {
            int dialogResult = JOptionPane.showConfirmDialog(null,
                    "Deleting this bucket will permanently erase all the data.\nDo you still want to delete it?",
                    "CAUTION!"
                    , JOptionPane.YES_NO_OPTION);
            if (dialogResult == JOptionPane.YES_OPTION) {
                s3.deleteBucket(bucket.name);
            }
        });

        panel.add(openButton);
        panel.add(uploadButton);
        panel.add(closeButton);
        panel.add(deleteButton);


        return panel;
    }

    static void displayChildren(FileFromAws file,java.util.List<S3ObjectSummary> summaries){
        Integer currentIndex = S3Panel.list.getSelectedIndex();
        ArrayList<String> filesAndFolders = new ArrayList<>();
        int offset=1;
        if (!file.isOpen ) {
            if ( file.type.equals("bucket"))
                for (S3ObjectSummary s : summaries
                        ) {
                    filesAndFolders.add(s.getKey());
                    if (s.getKey().split("/").length < 2) {
                        if (s.getKey().endsWith("/")) {
                            S3Panel.model.add(currentIndex + offset, new FileFromAws(s.getKey(), s.getBucketName(),
                                    s.getKey().split("/").length, "folder", s.getBucketName()));
                        } else {
                            S3Panel.model.add(currentIndex + offset, new FileFromAws(s.getKey(), s.getBucketName(),
                                    s.getKey().split("/").length, "file", s.getBucketName()));
                        }
                        offset++;
                    }
                }
            else if (file.type.equals("folder")) {
                for (S3ObjectSummary s : summaries
                        ) {
                    filesAndFolders.add(s.getKey());

                    if (s.getKey().startsWith(file.name) && (!s.getKey().equals(file.name))) {
                        if (s.getKey().endsWith("/")) {
                            S3Panel.model.add(currentIndex + offset, new FileFromAws(s.getKey(), s.getBucketName(),
                                    s.getKey().split("/").length, "folder", s.getBucketName()));
                        } else {
                            S3Panel.model.add(currentIndex + offset, new FileFromAws(s.getKey(), s.getBucketName(),
                                    s.getKey().split("/").length, "file", s.getBucketName()));
                        }
                        offset++;
                    }


                }
            } else if (file.type.equals("file")) {
                if (file.name.endsWith(".txt") || file.name.endsWith(".html") || file.name.endsWith(".sh") ||
                        file.name.endsWith(".bat") || file.name.endsWith(".css") || file.name.endsWith(".js") ||
                        file.name.endsWith(".c") || file.name.endsWith(".h") || file.name.endsWith(".cpp")) {
                    JSplitPane rightPanel = (JSplitPane) S3Panel.splitPane.getRightComponent();
                    AWSCredentials credentials = null;
                    try {
                        credentials = new ProfileCredentialsProvider("default").getCredentials();
                    } catch (Exception e) {
                        throw new AmazonClientException(
                                "something went wrong with your credentials",
                                e);
                    }
                    AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(getBucketLocation(file.parentBucket))
                            .build();

                    S3Object object = s3.getObject(
                            new GetObjectRequest(file.parentBucket, file.name));
                    InputStream objectData = object.getObjectContent();
                    InputStreamReader inputReader = new InputStreamReader(objectData);
                    BufferedReader reader = new BufferedReader(inputReader);
                    String s = null;

                    StringBuilder sb = new StringBuilder();
                    try {
                        while ((s = reader.readLine()) != null) {
                            sb.append(s);
                            sb.append("\n");
                        }
                        JTextArea textArea = new JTextArea(sb.toString());
                        JScrollPane scrollPane = new JScrollPane(textArea);
                        JPanel panel = new JPanel();
                        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
                        JPanel panelButtons = new JPanel();
                        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.X_AXIS));
                        panelButtons.add(new JButton("Save"));
                        panelButtons.add(new JButton("Download"));

                        panel.add(panelButtons);
                        panel.add(scrollPane);
                        rightPanel.setBottomComponent(panel);
                        file.isOpen = true;
                        for (int i = 0; i < S3Panel.model.getSize(); i++) {
                            if (S3Panel.model.get(i).type.equals("file"))
                                S3Panel.model.get(i).isOpen = false;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }else if (file.name.endsWith(".png") || file.name.endsWith(".jpg")){
                    JLabel image = new JLabel();
                    AWSCredentials credentials = null;
                    try {
                        credentials = new ProfileCredentialsProvider("default").getCredentials();
                    } catch (Exception e) {
                        throw new AmazonClientException(
                                "something went wrong with your credentials",
                                e);
                    }
                    AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                            .withCredentials(new AWSStaticCredentialsProvider(credentials))
                            .withRegion(getBucketLocation(file.parentBucket))
                            .build();

                    S3Object object = s3.getObject(
                            new GetObjectRequest(file.parentBucket, file.name));
                    InputStream objectData = object.getObjectContent();
                    Image img = null;
                    try {
                        img = ImageIO.read(objectData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image.setIcon(new ImageIcon(img));
                    JSplitPane rightPanel = (JSplitPane) S3Panel.splitPane.getRightComponent();
                    JScrollPane scrollPane = new JScrollPane(image);
                    rightPanel.setBottomComponent(scrollPane);
                }
            }
            file.isOpen = true;
        }else{
            int i = S3Panel.list.getSelectedIndex();
            int degree = S3Panel.model.get(i).degree;
            while(S3Panel.model.get(i+1).degree>degree){
                S3Panel.model.remove(i+1);
            }
            file.isOpen=false;
        }
    }

    static String getBucketLocation(String name) {
        AWSCredentials credentials = null;
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "something went wrong with your credentials",
                    e);
        }
        AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion("eu-west-1")
                .build();
        return s3.getBucketLocation(name);

    }

    public static class Infos {
        public Integer numberOfFiles;
        public Integer totalSize;
    }

    public static JPanel createTextArea(String content){

        return null;
    }
}
