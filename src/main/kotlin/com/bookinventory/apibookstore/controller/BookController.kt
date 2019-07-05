package com.bookinventory.apibookstore.controller

import com.bookinventory.apibookstore.WebClientApi
import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.GooleBook
import com.bookinventory.apibookstore.repository.BookRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class BookController(private val repository: BookRepository, private val webClientApi: WebClientApi){



    @GetMapping("/books")
    fun getAllBooks()=repository.findAll()

    @PostMapping("books")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveBook(@RequestBody book: Book) = repository.save(book)

    @GetMapping("/books/{id}")
    fun getBook(@PathVariable id:String): Mono<ResponseEntity<Book>> {
        return repository.findById(id)
                .map { book -> ResponseEntity.ok(book)  }
                .defaultIfEmpty(ResponseEntity.notFound().build())

    }

//    @PutMapping(value = "/books/{id}")
//    fun updateBook(@PathVariable id: String,
//                   @RequestBody book: Book): Mono<ResponseEntity<Book>>{
//        return repository!!.findById(id)
//                .flatMap { existingBook ->{
//                    existingBook.author=book.author
//                    repository.save(book)
//                }  }
//                .map{ ub ->ResponseEntity.ok(ub)}
//                .defaultIfEmpty(ResponseEntity.notFound().build())
//    }

    @PutMapping("/books/{id}")
    fun updateProduct(@PathVariable(value = "id") id: String,
                      @RequestBody book: Book): Mono<ResponseEntity<Book>> {
        return repository.findById(id)
                .flatMap { existingBook ->
//                    existingBook.title = book.title
                    existingBook.price = book.price
//                    existingBook.authors = book.authors
//                    existingBook.description = book.description
                    existingBook.quantity = book.quantity
                    repository.save(existingBook)
                }
                .map { updateProduct -> ResponseEntity.ok(updateProduct) }
                .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/books/{id}")
    fun deleteBook(@PathVariable id: String): Mono<ResponseEntity<Void>> {
        return repository.findById(id)
                .flatMap { book ->
                    repository.delete(book)
                            .then(Mono.just(ResponseEntity.ok().build<Void>()))
                }
                .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping("/books/api/{search}")
    fun getDataFromApi(@PathVariable search :String): Flux<GooleBook> {
        return webClientApi.getBookfromApi(search)
    }

    @GetMapping("/books/search/{query}")
    fun getBooksBySearch(@PathVariable query:String):Flux<Book>{
        return repository.findBookByTitleContaining(query)
    }


}