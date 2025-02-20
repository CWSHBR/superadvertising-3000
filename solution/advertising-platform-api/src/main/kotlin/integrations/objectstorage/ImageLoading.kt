package ru.cwshbr.integrations.objectstorage

import ru.cwshbr.utils.S3_BUCKET
import ru.cwshbr.utils.s3Client
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.*

object ImageLoading {
    fun saveImageToS3(imageName: String, bytes: ByteArray) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(S3_BUCKET)
                .key(imageName)
                .contentType("image/jpeg")
                .build(),
            RequestBody.fromBytes(bytes)

        )

    }

    fun deleteImageFromS3(imageName: String) {
        s3Client.deleteObject(
            DeleteObjectRequest.builder()
                .bucket(S3_BUCKET)
                .key(imageName)
                .build()
        )
    }

    fun getImageFromS3(imageName: String): InputStream? {
        val getObjectRequest = GetObjectRequest.builder()
            .bucket(S3_BUCKET)
            .key(imageName)
            .build()

        val response = s3Client.getObject(getObjectRequest, software.amazon.awssdk.core.sync.ResponseTransformer.toBytes())
            ?: return null

        return response.asInputStream()
    }
}