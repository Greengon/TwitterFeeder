package il.ac.colman.cs.util;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;

import java.awt.*;

public class AWSutil {
    /*
    Check this link: https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/setup-credentials.html
    for AWS credentials setting.
     */
    private static AWSCredentialsProvider awsCredentialsProvider;

    public static AmazonSQS getSQSClient(){
        awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(System.getProperty("aws.access.key.id"),System.getProperty("aws.secret.access.key"))
        );
        return AmazonSQSClientBuilder.standard().withRegion("us-east-1").withCredentials(awsCredentialsProvider).build();

    }

    public static AmazonS3 getS3Client(){
                awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(System.getProperty("aws.access.key.id"),System.getProperty("aws.secret.access.key"))
        );
        return AmazonS3ClientBuilder.standard().withRegion("us-east-1").withCredentials(awsCredentialsProvider).build();
    }

    public static AmazonCloudWatch getCloudWatchClient(){
        awsCredentialsProvider = new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(System.getProperty("aws.access.key.id"),System.getProperty("aws.secret.access.key")));
        return AmazonCloudWatchClientBuilder.standard().withRegion("us-east-1").withCredentials(awsCredentialsProvider).build();
    }

    // https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/examples-cloudwatch-publish-custom-metrics.html
    public static void takeCloudWatchData(AmazonCloudWatch cw,String dimensionName,String valueForDimension,String metricName,double data_point){

        Dimension dimension = new Dimension()
                .withName(dimensionName)
                .withValue(valueForDimension);

        MetricDatum datum = new MetricDatum()
                .withMetricName(metricName)
                .withUnit(StandardUnit.None)
                .withValue(data_point)
                .withDimensions(dimension);

        PutMetricDataRequest request = new PutMetricDataRequest()
                .withNamespace("gon-and-israel-watch")
                .withMetricData(datum);

        PutMetricDataResult response = cw.putMetricData(request);
    }
}
