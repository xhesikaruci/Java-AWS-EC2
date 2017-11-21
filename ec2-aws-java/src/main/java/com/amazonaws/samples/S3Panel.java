package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

public class S3Panel {
    static JSplitPane splitPane;
    JButton button = new JButton("toto");
    static JList<FileFromAws> list;
    static JScrollPane listPane;
    JTextArea textView = new JTextArea();
    JScrollPane textScroll;
    JPanel panel;
    static DefaultListModel<FileFromAws> model = new DefaultListModel<>();
    static Hashtable<String, String> locationsForBucket = new Hashtable<>();
    static Hashtable<FileFromAws, String> objectLocations;
    static Hashtable<String, ArrayList<String>> bucketStructure = new Hashtable<>();
    static ArrayList<FileFromAws> bucketList = new ArrayList<>();


    public S3Panel() {
        javax.swing.SwingUtilities.invokeLater(this::populateListWithBucketsFromWeb);
        textScroll = new JScrollPane(textView);
        list = new JList<>(model);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        list.setDragEnabled(true);

        listPane = new JScrollPane(list);
        listPane.getViewport().setViewPosition(new Point(20, 20));
        listPane.setPreferredSize(new Dimension(120, 250));


        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listPane, new JLabel("select a file to open it here"));
        splitPane.setResizeWeight(.2d);
        list.setCellRenderer(new FileRenderer());
        list.addListSelectionListener(arg0 -> {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if ((!arg0.getValueIsAdjusting()) && list.getSelectedValue().parent == null) {
                        splitPane.setRightComponent(S3utilities.getInformationAboutBucket(list.getSelectedValue()));
                    }
                }
            });

        });

        panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        panel.add(splitPane);
        panel.add(button);
    }

    void populateListWithBucketsFromWeb() {
        model.clear();
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

        FileFromAws file = null;

        for (Bucket b : s3.listBuckets()
                ) {
            file = new FileFromAws(b.getName(), "");
            model.addElement(file);
            bucketList.add(file);
        }
    }

    void populateListWithBucketsFromCache() {
        model.clear();
        for (FileFromAws f : bucketList
                ) {
            model.addElement(f);
        }

    }


    void removeAllChildren(FileFromAws file) {
        ArrayList<Integer> toRemove = new ArrayList<>();
        for (int i = 0; i < model.size(); i++) {
            if (model.get(i).type.equals("folder") && model.get(i).parent.equals(file.name)) {
                removeAllChildren(model.get(i));
            }
            if (model.get(i).parent.equals(file.name)) {
                toRemove.add(i);
            }
        }
        for (int i = toRemove.size() - 1; i > -1; i--) {
            model.remove(toRemove.get(i));
        }
        //TODO
    }


}


