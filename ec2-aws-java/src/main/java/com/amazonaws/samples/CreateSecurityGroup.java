package com.amazonaws.samples;

import com.amazonaws.services.ec2.*;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsRequest;
import com.amazonaws.services.ec2.model.DescribeSecurityGroupsResult;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.SecurityGroup;

/**
 * Creates an EC2 security group.
 */
public class CreateSecurityGroup
{
    public static void main(String[] args)
    {
        final String USAGE =
            "To run this example, supply a group name, group description and vpc id\n" +
            "Ex: CreateSecurityGroup <group-name> <group-description> <vpc-id>\n";

     /*   if (args.length != 3) {
            System.out.println(USAGE);
            System.exit(1);
        }
*/
        String group_name = "";
        String group_desc = "";
        String vpc_id = "";

         final AmazonEC2 ec2 = AmazonEC2ClientBuilder.defaultClient();

   /*     CreateSecurityGroupRequest create_request = new
            CreateSecurityGroupRequest()
                .withGroupName(group_name)
                .withDescription(group_desc)
                .withVpcId(vpc_id);

        CreateSecurityGroupResult create_response =
            ec2.createSecurityGroup(create_request);

        System.out.printf(
            "Successfully created security group named %s",
            group_name);

        IpRange ip_range = new IpRange()
            .withCidrIp("0.0.0.0/0");

        IpPermission ip_perm = new IpPermission()
            .withIpProtocol("tcp")
            .withToPort(80)
            .withFromPort(80)
            .withIpv4Ranges(ip_range);

        IpPermission ip_perm2 = new IpPermission()
            .withIpProtocol("tcp")
            .withToPort(22)
            .withFromPort(22)
            .withIpv4Ranges(ip_range);

        AuthorizeSecurityGroupIngressRequest auth_request = new
            AuthorizeSecurityGroupIngressRequest()
                .withGroupName(group_name)
                .withIpPermissions(ip_perm, ip_perm2);

        AuthorizeSecurityGroupIngressResult auth_response =
            ec2.authorizeSecurityGroupIngress(auth_request);

        System.out.printf(
            "Successfully added ingress policy to security group %s",
            group_name);
            */
        String group_id = "sg-1d8a0077";

        DescribeSecurityGroupsRequest request =
            new DescribeSecurityGroupsRequest()
                .withGroupIds(group_id);

        DescribeSecurityGroupsResult response =
            ec2.describeSecurityGroups(request);
        System.out.println(response.getSecurityGroups().get(0).getGroupId());
//        for(SecurityGroup group : response.getSecurityGroups().get(0).getGroupId()) {
//            System.out.printf(
//                "Found security group with id %s, " +
//                "vpc id %s " +
//                "and description %s",
//                group.getGroupId(),
//                group.getVpcId(),
//                group.getDescription());
//        }
    }
}