package br.pucpr.authserver.files

import br.pucpr.authserver.users.User
import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileStorage {

    fun save (user: User, path: String, file: MultipartFile): String
    fun saveWithInputStream (user: User, path: String, imageByteArray: ByteArray): String
    fun load (path: String): Resource?
    fun urlFor (path: String): String
}