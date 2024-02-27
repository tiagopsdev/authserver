package br.pucpr.authserver.integration.getavatars

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*


@Component
class GravatarClient {


    // Function to calculate MD5 hash of an input string
    fun hash(input: String): String {

        val md = MessageDigest.getInstance("SHA256")
        return md.digest(input.toByteArray(StandardCharsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
    }

    // Function to get Gravatar URL for a given email
    fun gravatarUrl(email: String, size: Int = 200): String {
        val emailHash = hash(email.trim().lowercase(Locale.getDefault()))
        log.info("emailHash ${emailHash}")
        return "$URL_GRAVATAR$emailHash?s=$size&d=404"
    }

    companion object{

        private const val URL_GRAVATAR = "https://www.gravatar.com/avatar/"
        private val log = LoggerFactory.getLogger(GravatarClient::class.java)


    }


}