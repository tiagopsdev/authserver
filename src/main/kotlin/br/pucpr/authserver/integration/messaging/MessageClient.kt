package br.pucpr.authserver.integration.messaging

import br.pucpr.authserver.users.User
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.regions.Regions
import com.amazonaws.services.sns.AmazonSNSAsync
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder
import com.amazonaws.services.sns.model.PublishRequest
import com.amazonaws.services.sns.model.SetSMSAttributesRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MessageClient {

    private val sns: AmazonSNSAsync = AmazonSNSAsyncClientBuilder.standard()
        .withRegion(Regions.US_EAST_1)
        .withCredentials(EnvironmentVariableCredentialsProvider())
        .build()

    fun sndSMS(user: User, text: String, important: Boolean = false): Boolean {

        if (user.phone.isBlank()) return false
        try {

                val attributes = SetSMSAttributesRequest().apply {
                    attributes = mapOf("DefaultSMSType" to if (important) "Transactional" else "Promotional")
                }

                sns.setSMSAttributes(attributes)

                sns.publishAsync(
                    PublishRequest().apply {
                        phoneNumber = user.phone
                        message = text

                    }
                )
                log.info("SMS sent SMS to ${user.name}: $text\"")
            return true

        } catch (error: Exception) {

            log.error("Could not send SMS to ${user.name}: $text", error)
            return false

        }


    }

    companion object {
        private val log = LoggerFactory.getLogger(MessageClient::class.java)
    }


}