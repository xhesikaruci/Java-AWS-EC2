package com.amazonaws.samples;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeRegionsResult;
import com.amazonaws.services.ec2.model.Region;

import java.util.Arrays;

/**
 * Describes all regions and zones
 */
public class list_regions
{
    public String[] main(String [] args)
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

         // Create the AmazonEC2Client object so we can call various APIs.
         AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
             .withCredentials(new AWSStaticCredentialsProvider(credentials))
             .withRegion("eu-central-1")
             .build();
    	
    	
         String[] places = new String [15];
         int i=0;
        DescribeRegionsResult regions_response = ec2.describeRegions();

        for(Region region : regions_response.getRegions()) {
          // System.out.println(region.getRegionName()); 
           places[i]= region.getRegionName();
         //  System.out.println(places[i]);
          i++;
          
        }

        	System.out.println(Arrays.toString(places));
        	return places;
       
    }
}