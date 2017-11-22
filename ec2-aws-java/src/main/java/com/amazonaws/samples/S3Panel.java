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
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;

public class S3Panel {
    static JSplitPane splitPane;
    static JList<FileFromAws> list;
    static JScrollPane listPane;
    JTextArea textView = new JTextArea();
    JScrollPane textScroll;
    JPanel panel;
    static Hashtable<String,java.util.List<S3ObjectSummary>> infos = new Hashtable<>();
    static DefaultListModel<FileFromAws> model = new DefaultListModel<>();
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
                        splitPane.setRightComponent(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                S3utilities.getInformationAboutBucket(list.getSelectedValue()),null));
                    }
                }
            });

        });

        list.addMouseListener(new MouseListener(){
                public void mouseClicked(MouseEvent e)
                {
                    JList theList = (JList) e.getSource();
                    if (e.getClickCount() == 2)
                    {
                        int index = theList.locationToIndex(e.getPoint());
                        if (index >= 0)
                        {
                            Object o = theList.getModel().getElementAt(index);
                            System.out.println("Double-clicked on: " + o.toString());
                            if (infos.get(o.toString())!=null)
                                S3utilities.displayChildren((FileFromAws) o,infos.get(o.toString()));
                            else
                                S3utilities.displayChildren((FileFromAws) o,infos.get(((FileFromAws) o).parent));

                        }
                    }
                }

        });


        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));
        panel.add(splitPane);

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


