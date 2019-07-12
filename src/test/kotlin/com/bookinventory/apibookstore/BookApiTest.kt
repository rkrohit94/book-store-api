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
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

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
        val book1 = Book("1", "Game of Thrones", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)
        val book2 = Book("2", "Sapiens", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)


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
    fun `should be able to find all books`() {

        webTestClient.get()
                .uri("/books")
                .exchange()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                        assert(list.responseBody!!.size >1)
                }

    }

    @Test
    fun `should be able to find book by Id`() {

        webTestClient.get()
                .uri("/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals("1",list.responseBody!!.first().id)
                }

    }



    @Test
    fun `should be able to save book`() {

        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book = Book("3", "Making India Awesome", listOf("Chetan Bhagat"), "desc", 1.00, 10, imageUrl)

        webTestClient.post()
                .uri("/books")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book), Book::class.java)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON_UTF8)
                .expectBody()
//                .jsonPath("$.id").isNotEmpty()
//                .jsonPath("$.title").isEqualTo("Making India Awesome")


    }

    @Test
    fun `should be able to update book quantity and price`() {
        val imageUrl = ImageUrl("xyz.com","abc.com")
        val book = Book("1", "Making India Awesome", listOf("Chetan Bhagat"), "desc", 2.00, 20, imageUrl)

        webTestClient.put()
                .uri("/books/1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(book), Book::class.java)
                .exchange()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals(20, list.responseBody!!.first().quantity)
                    assertEquals(2.00, list.responseBody!!.first().price)
                }

    }


//    @Test
//    fun `should not be able to update book quantity and price for invalid id`() {
//        val imageUrl = ImageUrl("xyz.com","abc.com")
//        val book = Book("1", "Making India Awesome", listOf("Chetan Bhagat"), "desc", 2.00, 20, imageUrl)
//
//        webTestClient.put()
//                .uri("/books/123")
//                .contentType(MediaType.APPLICATION_JSON_UTF8)
//                .accept(MediaType.APPLICATION_JSON_UTF8)
//                .body(Mono.just(book), Book::class.java)
//                .exchange()
//                .expectStatus().isNotFound
//
//    }




    @Test
    fun `should be able to search book by title`() {

        webTestClient.get()
                .uri("/books/search/Sap")
                .exchange()
                .expectBodyList(Book::class.java)
                .consumeWith<WebTestClient.ListBodySpec<Book>>{list ->
                    assertEquals(1, list.responseBody!!.size)
                    assertEquals("2",list.responseBody!!.first().id)
                    assertEquals("Sapiens",list.responseBody!!.first().title)
                }
    }



    @Test
    fun `should be able to delete book by Id`() {

        webTestClient.delete()
                .uri("/books/1")
                .exchange()
                .expectStatus().isOk

    }

//    @Test
//    fun `should not be able to delete book for invalid Id`() {
//
//        webTestClient.delete()
//                .uri("/books/123")
//                .exchange()
//                .expectStatus().isNotFound
//
//    }


}
