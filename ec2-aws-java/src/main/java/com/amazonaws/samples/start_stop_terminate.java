package com.amazonaws.samples;

import java.util.Scanner;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
public class start_stop_terminate {
	
	       //TERMINATE Instance function
	        public static void terminateInstance(String instance_id){
	        	
	        	final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

	           DryRunSupportedRequest<StartInstancesRequest> dry_request =
	                () -> {
	                StartInstancesRequest request = new StartInstancesRequest()
	                    .withInstanceIds(instance_id);

	                return request.getDryRunRequest();
	            };

	            DryRunResult dry_response = ec2.dryRun(dry_request);

	            if(!dry_response.isSuccessful()) {
	                System.out.printf(
	                    "Failed dry run to start instance %s", instance_id);

	                throw dry_response.getDryRunResponse();
	            }

	            TerminateInstancesRequest request = new TerminateInstancesRequest()
	                .withInstanceIds(instance_id);

	            ec2.terminateInstances(request);

	            System.out.printf("Successfully terminated instance %s", instance_id);
	        
	    }
	        
	        //STOP Instance Function
	        
	        public static void stopInstance(String instance_id)
	        {
	            final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

	            DryRunSupportedRequest<StopInstancesRequest> dry_request =
	                () -> {
	                StopInstancesRequest request = new StopInstancesRequest()
	                    .withInstanceIds(instance_id);

	                return request.getDryRunRequest();
	            };

	            DryRunResult dry_response = ec2.dryRun(dry_request);

	            if(!dry_response.isSuccessful()) {
	                System.out.printf(
	                    "Failed dry run to stop instance %s", instance_id);
	                throw dry_response.getDryRunResponse();
	            }

	            StopInstancesRequest request = new StopInstancesRequest()
	                .withInstanceIds(instance_id);

	            ec2.stopInstances(request);

	            System.out.printf("Successfully stop instance %s", instance_id);
	        }

	        //START Instance function
	        
	        public static void startInstance(String instance_id)
	        {
	            final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

	            DryRunSupportedRequest<StartInstancesRequest> dry_request =
	                () -> {
	                StartInstancesRequest request = new StartInstancesRequest()
	                    .withInstanceIds(instance_id);

	                return request.getDryRunRequest();
	            };

	            DryRunResult dry_response = ec2.dryRun(dry_request);

	            if(!dry_response.isSuccessful()) {
	                System.out.printf(
	                    "Failed dry run to start instance %s", instance_id);
	                throw dry_response.getDryRunResponse();
	            }

	            StartInstancesRequest request = new StartInstancesRequest()
	                .withInstanceIds(instance_id);

	            ec2.startInstances(request);

	            System.out.printf("Successfully starting instance %s", instance_id);
	        }

	        public static void main(String[] args)
	        {

	        	System.out.println("Provide instance ID you wish to change the status of");
	        	 Scanner sc = new Scanner(System.in);
	             String instance_id = sc.nextLine();
	             System.out.println("Provide status to which you wish to change to:\n Enter 1 to Start instance\n Enter 2 to Stop Instance\n Enter 3 to Terminate Instance\n");
	             Scanner sc1 = new Scanner(System.in);
	             int status = sc1.nextInt();
	       
	    		
	            //boolean start;
	    		switch(status){ 
	    		case 1 : startInstance (instance_id);
	    				break;
	    		case 2: 
	    		stopInstance(instance_id);
	    		break;
	    		case 3:
	    		terminateInstance(instance_id);
	    		break;
	    		}
	          
	    }

}