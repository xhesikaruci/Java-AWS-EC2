package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//this class regroups all the utilities for S3, such as list object, listing bucket, get file, put file
public class S3utilities {


    /**
     * @param region region in which the client will be created
     * @return client to work with s3
     */
    static AmazonS3 createClient(String region) {
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
                .withRegion(region)
                .build();

        return s3;
    }


    /**
     * @param bucket the bucket you want information about
     * @return returns a panel which display basic informations about the bucket and some buttons to take actions on it
     */
    static JPanel getInformationAboutBucket(FileFromAws bucket) {
        int size = 0;
        int numberOfFiles = 0;
        JPanel panel = new JPanel();
        java.util.List<S3ObjectSummary> summaries;

        AmazonS3 s3 = createClient(getBucketLocation(bucket.name));

        if (S3Panel.infos.get(bucket.name) == null) {

            System.out.println("from web");
            ObjectListing listing = s3.listObjects(bucket.name);
            summaries = listing.getObjectSummaries();
            S3Panel.infos.put(bucket.name, summaries);

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

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel name;
        if (bucket.parentBucket != null) {
            name = new JLabel("Name: " + bucket.parentBucket);
        } else {
            name = new JLabel("Name: " + bucket.name);
        }
        JButton openButton = new JButton("Open this bucket");
        JButton deleteButton = new JButton("Delete this bucket");
        JButton uploadButton = new JButton("Upload files to this bucket");

        openButton.addActionListener(actionEvent -> {
            displayChildren(bucket, summaries);
        });

        uploadButton.addActionListener(actionEvent -> {
            //open a file chooser popup
            JFileChooser fileChooser = new JFileChooser();
            int returnval = fileChooser.showOpenDialog(S3Panel.splitPane.getRightComponent());
            if (returnval == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                System.out.println("Opening: " + file.getAbsolutePath() + ".");
                ArrayList<String> folders = new ArrayList<>();
                folders.add("");
                System.out.println(name.getText());
                for (S3ObjectSummary s:S3Panel.infos.get(name.getText().split(" ")[name.getText().split(" ").length-1])
                     ) {
                    if (s.getKey().endsWith("/")){
                        folders.add(s.getKey());
                    }
                }

                //get the folder they want to upload to
                String input = (String) JOptionPane.showInputDialog(null, "Folder to upload to",
                        "Folder choice", JOptionPane.QUESTION_MESSAGE, null,
                        folders.toArray(), // Array of choices
                        folders.toArray()[0]); // Initial choice
                //if there is an answer, upload the file previously chosen
                if (input!=null){
                    uploadFileToBucket(name.getText().split(" ")[name.getText().split(" ").length-1],
                            input+file.getName(),file.getAbsolutePath());
                    populateListWithBucketsFromWeb();
                }

            }
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

        panel.add(name);
        panel.add(new JLabel("Size: " + size));
        panel.add(new JLabel("number of objects: " + numberOfFiles));
        panel.add(openButton);
        panel.add(uploadButton);
        panel.add(deleteButton);


        return panel;
    }


    /**
     * this function populates the list
     *
     * @param file      the file/folder/bucket you want to display children/content in the list
     * @param summaries list of all the elements present in the top level bucket
     */

    static void displayChildren(FileFromAws file, java.util.List<S3ObjectSummary> summaries) {
        Integer currentIndex = S3Panel.list.getSelectedIndex();
        ArrayList<String> filesAndFolders = new ArrayList<>();
        int offset = 1;
        System.out.println(file.toString() + " is open: " + file.isOpen);
        if (!file.isOpen) {
            displayChildrenBucket(file, summaries);

            displayChildrenFolder(file, summaries);

            displayFile(file, summaries);

            file.isOpen = true;
            int i = S3Panel.list.getSelectedIndex();

            S3Panel.model.get(i).isOpen = true;


        } else {
            int i = S3Panel.list.getSelectedIndex();
            int degree = S3Panel.model.get(i).degree;
            while (S3Panel.model.get(i + 1).degree > degree) {
                S3Panel.model.remove(i + 1);
            }
            S3Panel.model.get(i).isOpen = false;
            file.isOpen = false;
        }
    }


    static void displayChildrenBucket(FileFromAws file, java.util.List<S3ObjectSummary> summaries) {
        int offset = 1;
        Integer currentIndex = S3Panel.list.getSelectedIndex();
        file.isOpen = true;
        if (file.type.equals("bucket"))
            if (!(S3Panel.model.get(currentIndex + 1).degree > 0)) {
                for (S3ObjectSummary s : summaries
                        ) {
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
            } else {
                int i = S3Panel.list.getSelectedIndex();
                int degree = S3Panel.model.get(i).degree;
                while (S3Panel.model.get(i + 1).degree > degree) {
                    S3Panel.model.remove(i + 1);
                }
            }
    }

    static void displayChildrenFolder(FileFromAws file, java.util.List<S3ObjectSummary> summaries) {
        int offset = 1;
        Integer currentIndex = S3Panel.list.getSelectedIndex();
        if (file.type.equals("folder")) {
            for (S3ObjectSummary s : summaries
                    ) {

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
        }
    }

    static void displayFile(FileFromAws file, java.util.List<S3ObjectSummary> summaries) {
        FileFromAws bucket = null;
        for (FileFromAws f:S3Panel.bucketList
             ) {
            if (f.name.equals(file.parentBucket)){
                bucket = f;
                break;
            }
        }


        int offset = 1;
        Integer currentIndex = S3Panel.list.getSelectedIndex();
        if (file.type.equals("file")) {
            S3Panel.splitPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            if (file.name.endsWith(".txt") || file.name.endsWith(".html") || file.name.endsWith(".sh") ||
                    file.name.endsWith(".bat") || file.name.endsWith(".css") || file.name.endsWith(".js") ||
                    file.name.endsWith(".c") || file.name.endsWith(".h") || file.name.endsWith(".cpp")) {
                JSplitPane rightPanel = (JSplitPane) S3Panel.splitPane.getRightComponent();


                AmazonS3 s3 = createClient(getBucketLocation(file.parentBucket));
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

                    JButton downLoadButton = new JButton("Download");

                    downLoadButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            JFileChooser jf = new JFileChooser();
                            int returnValue =jf.showSaveDialog(null);
                            if (returnValue == JFileChooser.APPROVE_OPTION){
                                downLoadFileAndWrite(jf.getSelectedFile().getAbsolutePath(),s3,file);
                            }

                        }
                    });

                    panelButtons.add(downLoadButton);

                    JButton update = new JButton("Update S3");

                    panelButtons.add(update);

                    update.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            S3Object stringObject = new S3Object();
                            System.out.println(textArea.getText());
                            InputStream stream=null;

                            try {
                                stream = new ByteArrayInputStream(textArea.getText().getBytes(StandardCharsets.UTF_8.name()));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            try {
                                // Create temp file.
                                File temp = File.createTempFile(file.name.split("/")[file.name.split("/").length-1]
                                        , ".temporary");

                                // Delete temp file when program exits.
                                temp.deleteOnExit();

                                // Write to temp file
                                BufferedWriter out = new BufferedWriter(new FileWriter(temp));
                                out.write(textArea.getText());
                                out.close();
                                s3.putObject(new PutObjectRequest(file.parentBucket,S3Panel.lastFileOpened,temp));
                            } catch (IOException e) {
                            }



                        }
                    });

                    panel.add(panelButtons);
                    panel.add(scrollPane);

                    rightPanel.setBottomComponent(panel);
                    rightPanel.setTopComponent(getInformationAboutBucket(bucket));
                    file.isOpen = true;
                    for (int i = 0; i < S3Panel.model.getSize(); i++) {
                        if (S3Panel.model.get(i).type.equals("file"))
                            S3Panel.model.get(i).isOpen = false;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (file.name.endsWith(".png") || file.name.endsWith(".jpg")) {
                JLabel image = new JLabel();

                AmazonS3 s3 = createClient(getBucketLocation(file.parentBucket));

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
                for (int i = 0; i < S3Panel.model.getSize(); i++) {
                    if (S3Panel.model.get(i).type.equals("file"))
                        S3Panel.model.get(i).isOpen = false;
                }
            }
        }
        S3Panel.splitPane.setCursor(Cursor.getDefaultCursor());
    }

    /**
     * @param name name of the bucket
     * @return returns the string corresponding to the aws location
     */

    static String getBucketLocation(String name) {
        AmazonS3 s3 = createClient("eu-west-1");
        return s3.getBucketLocation(name);
    }


    static void downLoadFileAndWrite(String path,AmazonS3 s3,FileFromAws file){
        S3Object object = s3.getObject(
                new GetObjectRequest(file.parentBucket, file.name));
        InputStream in = object.getObjectContent();
        int count;
        byte[] buf = new byte[1024];
        OutputStream out = null;
        try {
            out = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while( (count = in.read(buf)) != -1)
            {
                out.write(buf, 0, count);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Infos {
        public Integer numberOfFiles;
        public Integer totalSize;
    }

    public static JPanel createTextArea(String content) {

        return null;
    }


    /**
     * @param bucketName bucket you want to upload to
     * @param fileName   name of the file once it's uploaded to s3
     * @param path       path on your machine
     */
    public static void uploadFileToBucket(String bucketName, String fileName, String path) {

        AmazonS3 s3 = createClient(getBucketLocation(bucketName));

        s3.putObject(new PutObjectRequest(bucketName, fileName, new File(path)));

    }


    /**
     * fetch the list of your buckets in aws and populate the list on the left panel
     */
    static void populateListWithBucketsFromWeb() {
        S3Panel.model.clear();
        S3Panel.infos.clear();
        S3Panel.bucketList = new ArrayList<>();

        AmazonS3 s3 = createClient("eu-west-1");

        FileFromAws file = null;

        for (Bucket b : s3.listBuckets()
                ) {
            file = new FileFromAws(b.getName(), "");
            S3Panel.model.addElement(file);
            S3Panel.bucketList.add(file);
        }
    }

    /**
     * @param key        key of the file you wish to delete
     * @param bucketName name of the bucket where the file is located
     */
    public static void deleteFile(String key, String bucketName) {
        AmazonS3 s3 = createClient(getBucketLocation(bucketName));

        try {
            s3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (AmazonClientException ace) {
            System.out.println("couldnt delete your file in " + bucketName);
        }
    }

}
