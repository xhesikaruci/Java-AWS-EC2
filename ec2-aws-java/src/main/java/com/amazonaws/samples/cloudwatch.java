package com.amazonaws.samples;
import java.sql.Date;
import java.util.Arrays;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.MonitorInstancesRequest;
import com.amazonaws.services.ec2.model.UnmonitorInstancesRequest;

public class cloudwatch {
	public static void main (String[] args){
		
		String instance_id = "i-0e0a26cde398b3893";
        final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();
		final AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();
		
		//Enable Monitoring Cloudwatch

		MonitorInstancesRequest request = new MonitorInstancesRequest()
		        .withInstanceIds(instance_id);
		ec2.monitorInstances(request);
		System.out.println("Monitoring state enabled");

	        final GetMetricStatisticsRequest request1 = request(instance_id); 
	        final GetMetricStatisticsResult result = result(cw, request1);
	        toStdOut(result, instance_id);   
	    }

		//Disable Monitoring Cloudwatch
		
		
//		UnmonitorInstancesRequest request = new UnmonitorInstancesRequest()
//			    .withInstanceIds(instance_id);
//			ec2.unmonitorInstances(request);
//			System.out.println("Monitoring state disabled");
	
		
//		
//		boolean done = false;
//		while(!done) {
//		 ListMetricsRequest request1 = new ListMetricsRequest()
//		 .withMetricName)
//		 .withNamespace("AWS/EC2");
//		 ListMetricsResult response = cw.listMetrics(request1);
//		 for(Metric metric : response.getMetrics()) {
//		 System.out.printf(
//		 "Retrieved metric %s", metric.getMetricName());
//		 }
//		 request1.setNextToken(response.getNextToken());
//		 if(response.getNextToken() == null) {
//		 done = true;
//		 }
//		}
		
	    private static GetMetricStatisticsRequest request(final String instanceId) {
	        final long twentyFourHrs = 1000 * 60 * 60 * 24;
	        final int oneHour = 60 * 60;
	        return new GetMetricStatisticsRequest()
	            .withStartTime(new Date(new Date().getTime()- twentyFourHrs))
	            .withNamespace("AWS/EC2")
	            .withPeriod(oneHour)w
	            .withDimensions(new Dimension().withName("InstanceId").withValue(instanceId))
	            .withMetricName("CPUUtilization")
	            .withStatistics("Average", "Maximum")
	            .withEndTime(new Date());
	    }

	    private static GetMetricStatisticsResult result(
	            final AmazonCloudWatchClient client, final GetMetricStatisticsRequest request) {
	         return client.getMetricStatistics(request);
	    }

	    private static void toStdOut(final GetMetricStatisticsResult result, final String instanceId) {
	        System.out.println(result); // outputs empty result: {Label: CPUUtilization,Datapoints: []}
	        for (final Datapoint dataPoint : result.getDatapoints()) {
	            System.out.printf("%s instance's average CPU utilization : %s%n", instanceId, dataPoint.getAverage());      
	            System.out.printf("%s instance's max CPU utilization : %s%n", instanceId, dataPoint.getMaximum());
	        }

		
			}

