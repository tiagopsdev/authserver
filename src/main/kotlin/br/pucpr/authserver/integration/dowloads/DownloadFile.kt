package br.pucpr.authserver.integration.dowloads




import br.pucpr.authserver.files.FileToStream
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths

import java.nio.file.StandardCopyOption
import kotlin.io.path.Path


@Component
class DownloadFile() {

    fun downloadFile (url: String): ByteArray? {

        //val urlImage: URI = URI.create(url)

        try {



            val client = RestTemplate()
            val imageByteArray: ByteArray? = client.getForObject(url, ByteArray::class.java)
            val inputStream : InputStream = ByteArrayInputStream(imageByteArray)
            val path = Paths.get("tempavatar.png").toAbsolutePath()
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING)

            val file = FileToStream(imageByteArray!!.size, inputStream)
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