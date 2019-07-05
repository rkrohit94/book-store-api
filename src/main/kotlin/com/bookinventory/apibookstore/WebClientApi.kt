package com.bookinventory.apibookstore

import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.GooleBook
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Flux

@Service
class WebClientApi  {

//    private val webClient: WebClient

    val url:String="https://www.googleapis.com/books/v1/volumes"
    val key:String="AIzaSyAYY-K5_0uUFjuogZRGpWb__ME_ZpzZ2jE"

//    init {
//        this.webClient = WebClient.builder()
//                .baseUrl(url)
//                .build()
//    }

    fun getBookfromApi(query: String): Flux<GooleBook> {


        return WebClient.create(buildUrl(query ))
                .get()
                .retrieve()
                .bodyToFlux(GooleBook::class.java)
    }

    fun buildUrl(query: String): String {
        return UriComponentsBuilder.fromHttpUrl(url)
                .replaceQueryParam("q",query)
                .replaceQueryParam("key",key)
                .encode().toUriString()
    }
}
