package br.pucpr.authserver.integration.quotes

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate

@Component
class QuoteClient {

    fun randomQuote (): Quote? =

        try {

            val client = RestTemplate()
            val response = client.getForObject(
                RANDOM_URL,
                QuoteResponse::class.java
                )
            response?.data?.firstOrNull()


        }catch (error: RestClientException){

            log.error("Problems retrieve ramdom quote", error)
            null

        }

    companion object{

        private val log = LoggerFactory.getLogger(QuoteClient::class.java)
        private val RANDOM_URL = "https://quote-garden.onrender.com/api/v3/quotes/random"

    }

}