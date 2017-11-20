package com.amazonaws.samples;


import java.awt.List;
import java.util.Arrays;

import com.amazonaws.services.applicationdiscovery.model.CreateTagsRequest;
//import com.amazonaws.services.dynamodbv2.datamodeling.KeyPair;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairRequest;
import com.amazonaws.services.ec2.model.CreateKeyPairResult;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
//import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.KeyPair;
import com.amazonaws.services.ec2.model.KeyPairInfo;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.util.Base64;

public class create_instance {
	
    public static void main(String[] args)
    {
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
       
        String keyName= "my-key-pair-64";       
    	DescribeKeyPairsResult response1 =  ec2.describeKeyPairs();
    	System.out.println("Key found: " + response1.getKeyPairs().get(0).getKeyName());
    	
        String group_id = "sg-d2f67db8";
        DescribeSecurityGroupsRequest request =
                new DescribeSecurityGroupsRequest()
                    .withGroupIds(group_id);
            DescribeSecurityGroupsResult response = ec2.describeSecurityGroups(request);
            System.out.println("Securit Group found " + response.getSecurityGroups().get(0).getGroupId());

        RunInstancesRequest runInstancesRequest =
        		 new RunInstancesRequest();
        		runInstancesRequest.withImageId("ami-d74be5b8")
        		 .withInstanceType("t2.micro")
        		 .withMinCount(1)
        		 .withMaxCount(1)
        		 .withKeyName(response1.getKeyPairs().get(0).getKeyName())
        		 .withSecurityGroups(response.getSecurityGroups().get(0).getGroupName());
//        		 .withSecurityGroups;
        		RunInstancesResult result = ec2.runInstances(runInstancesRequest);
        		System.out.println("Instance Created");
    }
    

}
