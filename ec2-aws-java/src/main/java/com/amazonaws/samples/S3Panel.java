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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Hashtable;

public class S3Panel {
    static JSplitPane splitPane ;
    JButton button = new JButton("toto");
    static JList<String> list;
    static JScrollPane listPane;
    JTextArea textView = new JTextArea();
    JScrollPane textScroll;
    JPanel panel ;
    ArrayList<String> bucketList = new ArrayList<>();
    static DefaultListModel<String> model = new DefaultListModel<>();
    static Hashtable<String,String> hashtable = new Hashtable<>();
    static Hashtable<String,ArrayList<String>> bucketStructure = new Hashtable<>();


    public S3Panel (){



        textScroll = new JScrollPane(textView);
        list = new JList<>(model);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        list.setDragEnabled(true);

        listPane = new JScrollPane(list);
        listPane.getViewport().setViewPosition(new Point(20,20));
        listPane.setPreferredSize(new Dimension(120,250));

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPane,new JLabel("select a file to open it here"));
        splitPane.setResizeWeight(.2d);

        panel = new JPanel();
        panel.setLayout(new GridLayout(2,1));
        panel.add(splitPane);
        panel.add(button);
        list.addListSelectionListener(arg0 -> {
            if (!arg0.getValueIsAdjusting()) {
                if (!list.getSelectedValue().endsWith("/")) {
                    if (hashtable.get(list.getSelectedValue()) == null)
                        getBucketLocation(list.getSelectedValue());
                    while (hashtable.get(list.getSelectedValue()) == null) {

                    }
                    splitPane.setRightComponent(getBucketDescription(list.getSelectedValue()));
                }else{
                    Integer index=list.getSelectedIndex();
                    while(list.getModel().getElementAt(index).endsWith("/")){
                        index--;
                    }
                    ArrayList<String> files = bucketStructure.get(list.getModel().getElementAt(index));
                    for (String s:files
                         ) {
                        if ((!s.endsWith("/")) && s.startsWith(list.getSelectedValue().split(" ")[list.getSelectedValue().split(" ").length-1])){
                            System.out.println(list.getSelectedValue());
                            model.add(list.getSelectedIndex()+1,s);
                        }
                    }
                }
            }
        });

        listBucket();


    }

















    public JPanel getBucketDescription(String name){
        JPanel panel = new JPanel();
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
                .withRegion(hashtable.get(name))
                .build();



        int size = 0;
        int numberOfFiles = 0;

        ObjectListing listing = s3.listObjects( name );
        java.util.List<S3ObjectSummary> summaries = listing.getObjectSummaries();
        for (S3ObjectSummary s: summaries
                ) {
            System.out.println(s);
            size+=s.getSize();
            numberOfFiles++;
        }

        panel.add(new JLabel("Size: "+ size));
        panel.add(new JLabel("number of objects: "+ numberOfFiles));
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        JButton openButton = new JButton("Open this bucket");
        JButton deleteButton = new JButton("Delete this bucket");

        openButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Integer currentIndex =list.getSelectedIndex();
                ArrayList<String> filesAndFolders = new ArrayList<>();
                for (S3ObjectSummary s: summaries
                        ) {
                    filesAndFolders.add(s.getKey());
                    if (s.getKey().endsWith("/")){
                        model.add(currentIndex+1,"     "+s.getKey());
                    }
                }
                bucketStructure.put(name,filesAndFolders);
            }
        });


        panel.add(openButton);
        panel.add(deleteButton);


        return panel;
    }



    public String[] listBucket() {
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
                .withRegion("us-west-2")
                .build();

        ArrayList<String> buckets = new ArrayList<>();
        for (Bucket bucket : s3.listBuckets()) {
                System.out.println(bucket.getName());
                buckets.add(bucket.getName());
                model.addElement(bucket.getName());
        }

        String[] result=new String[buckets.size()];
        result=buckets.toArray(result);
        bucketList=buckets;

        return result;

    }

    void getBucketLocation(String name){
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

        hashtable.put(name,s3.getBucketLocation(name));
    }

}


