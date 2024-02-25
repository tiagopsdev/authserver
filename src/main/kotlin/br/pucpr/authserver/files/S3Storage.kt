package br.pucpr.authserver.files

import br.pucpr.authserver.users.User
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.ObjectMetadata
import com.amazonaws.services.s3.transfer.TransferManagerBuilder
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.io.InputStream

@Component
class S3Storage: FileStorage {

    private val s3: AmazonS3 = AmazonS3ClientBuilder.standard()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(EnvironmentVariableCredentialsProvider())
        .build()

    override fun save(user: User, path: String, file: MultipartFile): String {

        val contentType = file.contentType!!

        val transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3)
            .build()

        val bucket = if (path.startsWith("avatars")) THUMBS else PUBLIC
        val meta = ObjectMetadata()
        meta.contentType = contentType
        meta.contentLength = file.size
        meta.userMetadata["userID"] = "${user.id}"
        meta.userMetadata["originalFilename"] = file.originalFilename

        transferManager.upload(bucket, path, file.inputStream, meta)
            .waitForUploadResult()

        return path

    }

    override fun saveWithInputStream(user: User, path: String, imageByteArray: ByteArray): String {
        val contentType = "image/png"
        val inputStream : InputStream = ByteArrayInputStream(imageByteArray)

        val transferManager = TransferManagerBuilder.standard()
            .withS3Client(s3)
            .build()

        val bucket = if (path.startsWith("avatars")) THUMBS else PUBLIC
        val meta = ObjectMetadata()
        meta.contentType = contentType
        meta.contentLength = imageByteArray.size.toLong()
        meta.userMetadata["userID"] = "${user.id}"
        meta.userMetadata["originalFilename"] = "gravatar.png"

        transferManager.upload(bucket, path, inputStream, meta)
            .waitForUploadResult()

        return path
    }

    override fun load(path: String): Resource? =
        InputStreamResource(s3.getObject(PUBLIC,
            path.replace("-S-", "/")).objectContent)

    override fun urlFor(path: String) = "https://$PUBLIC.s3.amazonaws.com/$path"

    companion object {

        private const val THUMBS = "tps-authserver-thumbnails"
        private const val PUBLIC = "tps-authserver-public"


    }

}