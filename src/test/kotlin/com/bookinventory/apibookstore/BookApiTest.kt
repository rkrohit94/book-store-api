package com.bookinventory.apibookstore

import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.ImageUrl
import com.bookinventory.apibookstore.repository.BookRepository
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class BookApiTest{

    @Autowired lateinit var webTestClient: WebTestClient

    @Autowired lateinit var repository: BookRepository

    @Before
    fun SetUp(){
        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book1 = Book("123", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)
        val book2 = Book("1234", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)


        Flux.just(book1,book2)
                .flatMap { repository.save(it) }
                .thenMany ( repository.findAll() )
                .subscribe{println(it)}

    }

    @After
    fun tearDown(){
        repository.deleteAll()
    }

    @Test
    fun testGetAllBooks() {

        webTestClient.get()
                .uri("/books")
                .exchange()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                        assertEquals(2, list.responseBody!!.size)
                        assertEquals("123",list.responseBody!!.first().id)
                }

    }



}
