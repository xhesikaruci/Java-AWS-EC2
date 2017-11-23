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
    static Hashtable<String, java.util.List<S3ObjectSummary>> infos = new Hashtable<>();
    static DefaultListModel<FileFromAws> model = new DefaultListModel<>();
    static ArrayList<FileFromAws> bucketList = new ArrayList<>();
    static String lastFileOpened = "";
    FileFromAws todelete = null;


    public S3Panel() {
        javax.swing.SwingUtilities.invokeLater(S3utilities::populateListWithBucketsFromWeb);
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
                    try {
                        if ((!arg0.getValueIsAdjusting()) && list.getSelectedValue().type.equals("bucket")) {
                            splitPane.setRightComponent(new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                                    S3utilities.getInformationAboutBucket(list.getSelectedValue()), null));
                            for (int i = 0; i < model.getSize(); i++) {
                                if (model.get(i).type.equals("file"))
                                    model.get(i).isOpen = false;
                            }

                        }
                    }catch (Exception e){

                    }
                }
            });

        });

        JPopupMenu menu = new JPopupMenu("Delete");
        JMenuItem delete = new JMenuItem("Delete");
        delete.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                S3utilities.deleteFile(todelete.name, todelete.parentBucket);
                populateListWithBucketsFromWeb();
            }
        });

        menu.add(delete);

        list.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1 && SwingUtilities.isRightMouseButton(e)) {
                    menu.show(list, e.getX(), e.getY());
                    todelete = model.get(list.locationToIndex(e.getPoint()));
                    System.out.println("name: " + todelete.name);
                    System.out.println("bucket: " + todelete.parentBucket);

                }
                mouseEvent(e);
            }

        });


        panel = new JPanel();
        panel.setLayout(new GridLayout(1, 1));
        panel.add(splitPane);

    }

    void populateListWithBucketsFromWeb() {
        model.clear();
        infos = new Hashtable<>();
        bucketList = new ArrayList<>();
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

    private void mouseEvent(MouseEvent e) {
        JList theList = (JList) e.getSource();
        if (e.getClickCount() == 2) {
            int index = theList.locationToIndex(e.getPoint());
            if (index >= 0) {
                Object o = theList.getModel().getElementAt(index);

                if (((FileFromAws) o).type.equals("file")) {
                    lastFileOpened = ((FileFromAws) o).name;
                    System.out.println("last file opened is "+lastFileOpened);
                }

                if (infos.get(o.toString()) != null) {
                    S3utilities.displayChildren((FileFromAws) o, infos.get(o.toString()));

                } else
                    S3utilities.displayChildren((FileFromAws) o, infos.get(((FileFromAws) o).parent));

            }

        }
    }


}


