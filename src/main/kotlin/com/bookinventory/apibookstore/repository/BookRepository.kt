package com.bookinventory.apibookstore.repository

import com.bookinventory.apibookstore.model.Book
import org.springframework.data.mongodb.repository.CountQuery
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface BookRepository : ReactiveMongoRepository<Book,Long> {
    fun findById(id: String): Mono<Book>
    fun findBookByTitleContaining(query: String):Flux<Book>
}