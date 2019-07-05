package com.bookinventory.apibookstore

import com.bookinventory.apibookstore.controller.BookController
import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.GooleBook
import com.bookinventory.apibookstore.model.ImageUrl
import com.bookinventory.apibookstore.model.Item
import com.bookinventory.apibookstore.repository.BookRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

import io.mockk.mockk
import io.mockk.mockkObject
import org.mockito.ArgumentMatchers
//import io.mockk.verify
import org.mockito.Mockito
import org.mockito.Mockito.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RunWith(SpringRunner::class)
@SpringBootTest
class ApiBookstoreApplicationTests {

    val imageUrl =ImageUrl("xyz.com","abc.com")

    val book = Book("123", "abc", listOf("xyz","abc"), "desc", 1.00, 10, imageUrl)
    val item = Item(book)
    val gooleBook = GooleBook(listOf(item))

     var repository = mock(BookRepository::class.java)
     var webClientApi = mock(WebClientApi::class.java)

    @Test
    fun `should find all books`() {
        val bookFlux = Flux.just(book)
        `when`(repository.findAll()).thenReturn(bookFlux)
//        every { repository.findAll() } returns bookFlux

        val result = BookController(repository,webClientApi).getAllBooks()
        assert(result==bookFlux)

    }

    @Test
    fun `should save books`() {
        val bookFlux = Mono.just(book)
        `when`(repository.save(ArgumentMatchers.any(Book::class.java))).thenReturn(bookFlux)

        val result = BookController(repository,webClientApi).saveBook(book)
        assert(result==bookFlux)

    }

    @Test
    fun `should find book from Google Api`(){
        val fluxGooleBook = Flux.just(gooleBook)
        `when`(webClientApi.getBookfromApi(ArgumentMatchers.anyString())).thenReturn(fluxGooleBook)

        val result = BookController(repository,webClientApi).getDataFromApi("games")
        assert(result==fluxGooleBook)
    }

//    @Test
//    fun `should find book by id`(){
//        val bookMono = Mono.just(book)
//        every { repository.findById(any<String>()) } returns bookMono
//
//        val result = BookController(repository,webClientApi).getBook("xyz")
//        //verify { result }
//        assert(result==bookMono)
//    }

}
