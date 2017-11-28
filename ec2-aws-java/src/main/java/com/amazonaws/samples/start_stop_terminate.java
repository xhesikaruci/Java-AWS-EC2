package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;

public class start_stop_terminate {
	 
    public static void main(String instance_id, String status, String region)

    
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
		
        //boolean start;
		switch(status){ 
		case "Start" : if(status.equals("Start"))
		{  StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instance_id);

						StartInstancesResult response = ec2.startInstances(request);

						System.out.printf("Successfully starting instance %s", instance_id);
        
        	
		} else
			
				break;
		
		
		case "Stop": if(status.equals("Stop")){
			StopInstancesRequest request = new StopInstancesRequest()
        			.withInstanceIds(instance_id);

        	StopInstancesResult response = ec2.stopInstances(request);
        	
		}
		else	
				break;
		
		
		
		case "Terminate":	if(status.equals("Terminate"))
				{ 
			 TerminateInstancesRequest request = new TerminateInstancesRequest()
		                .withInstanceIds(instance_id);

		            TerminateInstancesResult response = ec2.terminateInstances(request);
				}
		else
				break;
		
		case "Reboot":if (status.equals("Reboot"))
		{ 
			 RebootInstancesRequest request = new RebootInstancesRequest()
		            .withInstanceIds(instance_id);

		            RebootInstancesResult response = ec2.rebootInstances(request);
			
		}
		}
    }     

}