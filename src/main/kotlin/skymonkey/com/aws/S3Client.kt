package skymonkey.com.aws

import skymonkey.com.EnvConfig
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import java.nio.file.Paths

class S3BucketClient {
    private val accessKeyId = EnvConfig.awsAccessKey
    private val secretAccessKey = EnvConfig.awsSecretKey
    private val bucketName = EnvConfig.awsBucketName

    private val s3: S3Client = S3Client.builder()
        .region(Region.US_EAST_1) // Set your region
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
        .build()

    fun uploadFile(keyName: String, filePath: String): PutObjectResponse {
        val putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(keyName)
            .build()

        return s3.putObject(putObjectRequest, Paths.get(filePath))
    }

    fun getPublicUrl(keyName: String): String {
        return "https://$bucketName.s3.amazonaws.com/$keyName"
    }
}