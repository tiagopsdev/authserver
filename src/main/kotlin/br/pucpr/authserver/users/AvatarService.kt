package br.pucpr.authserver.users

import br.pucpr.authserver.exception.UnsupportedMediaTypeException
import br.pucpr.authserver.files.FileStorage
import br.pucpr.authserver.files.FileToStream
import br.pucpr.authserver.files.S3Storage
import br.pucpr.authserver.integration.dowloads.DownloadFile
import br.pucpr.authserver.integration.getavatars.GravatarClient
import br.pucpr.authserver.integration.getavatars.UIAvatarClient
import org.jboss.logging.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class AvatarService (
    @Qualifier("fileStorage") val storage: FileStorage,
    val gravatarClient: GravatarClient,
    val uiAvatarClient: UIAvatarClient,
    val downloadFile: DownloadFile


) {

        fun save(user: User, avatar: MultipartFile): String =
            try {

                val contentType = avatar.contentType!!
                val extension = when(contentType){

                    "image/jpeg" -> "jpg"
                    "image/png" -> "png"
                    else -> throw UnsupportedMediaTypeException("jpeg", "png")

                }
                val name = "${user.id}/${user.id}"
                storage.save(user, "$FOLDER/$name.$extension", avatar)
                "${user.id}/m_${user.id}.png"


            }catch (exception: Error){

                log.error("Unable to store avatar of user ${user.id}! Using default!", exception)
                DEFAULT_AVATAR


            }

    fun saveWithInputStream(user: User, avatarIS: ByteArray): String =
        try {

            val extension = "png"


            val name = "${user.id}/${user.id}"
            storage.saveWithInputStream(user, "$FOLDER/$name.$extension", avatarIS)
            "${user.id}/m_${user.id}.png"


        }catch (exception: Error){

            log.error("Unable to store avatar of user ${user.id}! Using default!", exception)
            DEFAULT_AVATAR


        }

    fun load (name: String) = storage.load(name)

    fun urlFor (path: String) = storage.urlFor("$FOLDER/$path")

    fun getAvatar (user: User): String{

        var file: ByteArray?

        val gravatarURL = gravatarClient.gravatarUrl(user.email)
        file = downloadFile.downloadFile(gravatarURL)
        if (file != null) return saveWithInputStream(user, file)

        val uiAvatarUrl = uiAvatarClient.uiAvatarUrl(user.name)
        file = downloadFile.downloadFile(uiAvatarUrl)
        if (file != null) return saveWithInputStream(user, file)

        return DEFAULT_AVATAR


    }

    companion object {

        const val FOLDER = "avatars"
        const val DEFAULT_AVATAR = "default.jpg"
        private val log = LoggerFactory.getLogger(AvatarService::class.java)
    }


}