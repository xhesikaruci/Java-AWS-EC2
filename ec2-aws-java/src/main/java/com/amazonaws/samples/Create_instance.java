package com.amazonaws.samples;


import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
//import com.amazonaws.services.ec2.model.*;

public class Create_instance {
	
    public static void main(String region,  String ami_id) throws IOException
    {
     
        AWSCredentials credentials = null;
        try {
	      credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Kristi.KristianKuro-PC\\.aws\\credentials), and is in valid format.",
                    e);
        }
        
        AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        
        //Key Pairs
        String keyName= null;       
      System.out.println("#3 Describe Available Key Pairs");
      DescribeKeyPairsResult dkr = ec2.describeKeyPairs();
      java.util.List<KeyPairInfo> keys = dkr.getKeyPairs();
      System.out.println("You have " + keys.size() + " Amazon keys");
      
      if (keys.isEmpty())
      {
      
    	  CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
          keyName = "VR2337.pem";
          createKeyPairRequest.withKeyName(keyName);           	
          CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);
         	
          KeyPair keyPair = new KeyPair();
          keyPair = createKeyPairResult.getKeyPair();           		    	
          
          String privateKey = keyPair.getKeyMaterial();
          File keyFile = new File(keyName);
          FileWriter fw = new FileWriter(keyFile);
          fw.write(privateKey);
          fw.close(); }
      else {
         keyName=keys.get(0).getKeyName();
      }

    	
    	//Securty Groups
    	
      String groupName =null;
        DescribeSecurityGroupsResult security = ec2.describeSecurityGroups();
        java.util.List<SecurityGroup> groups = security.getSecurityGroups();
        System.out.println("You have"+ groups.size()+ "Amazon gorups");
        if (groups.isEmpty())
        {
        	groupName = "XhesiakRuci";
        	CreateSecurityGroupRequest request = new CreateSecurityGroupRequest();
        	request.withGroupName(groupName).withDescription("XhesikaRuci Security Group");
        	ec2.createSecurityGroup(request);
        	}
        else
        {
        	groupName= groups.get(0).getGroupName();
        	System.out.println(groupName);
        }
        

        RunInstancesRequest runInstancesRequest =
        		 new RunInstancesRequest();
        		runInstancesRequest.withImageId(ami_id)
        		 .withInstanceType("t2.micro")
        		 .withMinCount(1)
        		 .withMaxCount(1)
        		 .withKeyName(keyName)
        	//	 .withSecurityGroups(response.getSecurityGroups().get(0).getGroupName());
        		 .withSecurityGroups(groupName);
        		RunInstancesResult result = ec2.runInstances(runInstancesRequest);
        		System.out.println("Instance Created");
    }
    

}
