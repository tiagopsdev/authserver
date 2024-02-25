package br.pucpr.authserver.integration.getavatars

import br.pucpr.authserver.integration.quotes.QuoteClient
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class UIAvatarClient {

    fun uiAvatarUrl (name: String): String {

        val formattedName = FIELD_NAME + name.split(" ").let{

            it.take(2).joinToString("+")

        }
        log.info("UIAvatar URL - $UI_AVATAR_URL$formattedName&$FIELD_VALUE_SIZE")
        return "$UI_AVATAR_URL$formattedName&$FIELD_VALUE_SIZE"

    }

    companion object{

        private val UI_AVATAR_URL = "https://ui-avatars.com/api/?"
        private val FIELD_VALUE_SIZE = "size=200"
        private val FIELD_NAME = "name="
        private val log = LoggerFactory.getLogger(UIAvatarClient::class.java)

    }


}