package com.amazonaws.samples;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeKeyPairsResult;


public class list_key_pair {
	 public static void main(String[] args)
	    {
		 String keyName= "my-key-pair-64";
	final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
	DescribeKeyPairsResult response = ec2.describeKeyPairs();
	System.out.println(response.getKeyPairs().get(0).getKeyName());
//	for(KeyPairInfo key_pair : response.getKeyPairs()) {
//	 System.out.printf(
//	 "Found key pair with name %s " +
//	 "and fingerprint %s",
//	 key_pair.getKeyName(),
//	 key_pair.getKeyFingerprint());
//	}

  }
}