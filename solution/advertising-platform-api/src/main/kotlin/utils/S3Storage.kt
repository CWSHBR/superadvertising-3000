package ru.cwshbr.utils

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

val s3Client = S3Client.builder()
    .region(Region.of("aws-global"))
    .forcePathStyle(true)
    .endpointOverride(URI.create(S3_URL)) // Адрес вашего локального S3
    .credentialsProvider(StaticCredentialsProvider.create(
        AwsBasicCredentials.create(S3_ACCESS_KEY, S3_SECRET_KEY)))
    .build()