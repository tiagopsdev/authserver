package br.pucpr.authserver.integration.dowloads




import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


@Component
class DownloadFile() {

    fun downloadFile (url: String): ByteArray? {

        //val urlImage: URI = URI.create(url)

        try {



            val client = RestTemplate()
            val imageByteArray: ByteArray? = client.getForObject(url, ByteArray::class.java)
            val inputStream : InputStream = ByteArrayInputStream(imageByteArray)
            val root = Paths.get(ROOT)
            val destinationFile = root.resolve(TEMP_AVATAR_NAME)
                .normalize()
                .toAbsolutePath()
            Files.createDirectories(destinationFile.parent)
            //val path = Paths.get("tempavatar.png").toAbsolutePath()
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING)

           return imageByteArray



        } catch (error: RestClientException) {

            log.error("Problems download file from url ${url}}", error)
            return null


        }
    }



    companion object{

        private val TEMP_AVATAR_NAME = "tempAvatar.png"
        private val ROOT = ".\\tempAvatarFolder"
        private val log = LoggerFactory.getLogger(DownloadFile::class.java)


    }

}