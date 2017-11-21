package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

//this class regroups all the utilities for S3, such as list object, listing bucket, get file, put file
public class S3utilities {
    static JPanel getInformationAboutBucket(FileFromAws bucket){
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
                .withRegion(getBucketLocation(bucket.name))
                .build();



        int size = 0;
        int numberOfFiles = 0;

        ObjectListing listing = s3.listObjects( bucket.name );
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

        openButton.addActionListener(actionEvent -> {
            Integer currentIndex =S3Panel.list.getSelectedIndex();
            ArrayList<String> filesAndFolders = new ArrayList<>();

            if (!bucket.isOpen)
            for (S3ObjectSummary s: summaries
                    ) {
                filesAndFolders.add(s.getKey());

                if (s.getKey().split("/").length < 2) {
                    if (s.getKey().endsWith("/")) {
                        S3Panel.model.add(currentIndex + 1, new FileFromAws(s.getKey(), s.getBucketName(), s.getKey().split("/").length, "folder"));
                    } else {
                        S3Panel.model.add(currentIndex + 1, new FileFromAws(s.getKey(), s.getBucketName(), s.getKey().split("/").length, "file"));
                    }
                }
            }

            bucket.isOpen=true;

        });

        deleteButton.addActionListener(actionEvent -> {
            int dialogResult =JOptionPane.showConfirmDialog(null,
                    "Deleting this bucket will permanently erase all the data.\nDo you still want to delete it?",
                    "CAUTION!"
                    , JOptionPane.YES_NO_OPTION);
            if (dialogResult==JOptionPane.YES_OPTION){
                s3.deleteBucket(bucket.name);
            }
        });


        panel.add(openButton);
        panel.add(deleteButton);


        return panel;
    }

    static String getBucketLocation(String name){
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
}
