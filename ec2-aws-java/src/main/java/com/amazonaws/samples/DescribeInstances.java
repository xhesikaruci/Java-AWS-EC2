package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
 * Describes all EC2 instances associated with an AWS account
 */
public class DescribeInstances
{
    public  String[] main(String region)
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

       DescribeInstancesRequest request = new DescribeInstancesRequest();
       int i=0;
            DescribeInstancesResult response = ec2.describeInstances(request);  
           String[] description = new String[10];
        
           
            for(Reservation reservation : response.getReservations()) {
            	
                for(Instance instance : reservation.getInstances()) {

                      description[i]=  instance.getInstanceId() + "  Type:" + instance.getInstanceType() + "  Status:" + instance.getState().getName()+ "  Monitoring status:" + instance.getMonitoring().getState();
                      i++;	

                }
                
                System.out.println(description[i]);
            }

            request.setNextToken(response.getNextToken());
         
	
            if(response.getNextToken() == null) {
               // done = true;
            }
            return description;
        }
    }
