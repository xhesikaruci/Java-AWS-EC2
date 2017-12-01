package com.amazonaws.samples;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;

public class Network_In {
    public String[] main(String instance_id, String region) {

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
        final  AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();

        final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(region)
                .build();
        MonitorInstancesRequest request1 = new MonitorInstancesRequest()
                .withInstanceIds(instance_id);
        ec2.monitorInstances(request1);

        final GetMetricStatisticsRequest request = request(instance_id);
        final GetMetricStatisticsResult result = result(cw, request);
        return toStdOut(result, instance_id);
    }


    private static GetMetricStatisticsRequest request(final String instanceId) {
        final long twentyFourHrs = 1000 * 60 * 60 * 24;
        final int oneHour = 60 * 60;
        System.out.println(new Date(new Date().getTime()-twentyFourHrs));
        System.out.println(new Date());
        return new GetMetricStatisticsRequest()
                .withStartTime(new Date(new Date().getTime()-twentyFourHrs))
                .withNamespace("AWS/EC2")
                .withPeriod(oneHour)
                .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
                .withMetricName("NetworkIn")
                .withStatistics("Average", "Maximum")
                .withEndTime(new Date());
    }

    private static GetMetricStatisticsResult result(
            final AmazonCloudWatch cw, final GetMetricStatisticsRequest request) {
        return cw.getMetricStatistics(request);
    }

    private static String[] toStdOut(final GetMetricStatisticsResult result, final String instanceId) {
        System.out.println(result); // outputs empty result: {Label: CPUUtilization,Datapoints: []}
        int i=0;
        String [] description = new String[48];
        for (final Datapoint dataPoint : result.getDatapoints()) {
            description [i] = String.valueOf(dataPoint.getAverage());
            System.out.println(description[i]);

            i++;
//	        	System.out.printf("%s instance's average CPU utilization : %s%n", instanceId, dataPoint.getAverage(), dataPoint.getTimestamp());
//	            System.out.printf("%s instance's max CPU utilization : %s at time: %s%n", instanceId, dataPoint.getMaximum(), dataPoint.getTimestamp().getTime());

        }
        return description;

    }

}