package com.bookinventory.apibookstore.controller

import com.bookinventory.apibookstore.WebClientApi
import com.bookinventory.apibookstore.model.Book
import com.bookinventory.apibookstore.model.GooleBook
import com.bookinventory.apibookstore.repository.BookRepository
import com.bookinventory.apibookstore.service.AuditService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono


@RestController
@CrossOrigin(origins = ["*"], allowedHeaders = ["*"])
class BookController(private val repository: BookRepository,
                     private val webClientApi: WebClientApi,
                     private val auditService: AuditService){



    @GetMapping("/books")
    fun getAllBooks()=repository.findAll()

    @PostMapping("/books")
    @ResponseStatus(HttpStatus.CREATED)
    fun saveBook(@RequestBody book: Book)= repository.save(book).subscribe(auditService::sendAddMessage)

    @GetMapping("/books/{id}")
    fun getBook(@PathVariable id:String): Mono<ResponseEntity<Book>> {
        return repository.findById(id)
                .map { book -> ResponseEntity.ok(book) }
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
    fun updateBook(@PathVariable(value = "id") id: String,
                      @RequestBody book: Book): Mono<Book> {
        val result = repository.findById(id)
                .flatMap { existingBook ->
                    existingBook.price = book.price
                    existingBook.quantity = book.quantity
                    repository.save(existingBook)
                }
//                .map { updateProduct -> ResponseEntity.ok(updateProduct) }
//                .defaultIfEmpty(ResponseEntity.notFound().build())
        result.subscribe(auditService::sendEditMessage)
        return result
    }

    @DeleteMapping("/books/{id}")
    fun deleteBook(@PathVariable id: String): Mono<Void> {
        val result =  repository.findById(id)
                     .subscribe(auditService::sendDeleteMessage)
              return repository.deleteById(id)

//                .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping("/books/api/{search}")
    fun getDataFromApi(@PathVariable search :String): Flux<GooleBook> {
        return webClientApi.getBookfromApi(search)
    }

    @GetMapping("/books/search/{query}")
    fun getBooksBySearch(@PathVariable query:String):Flux<Book>{
        return repository.findBookByTitleContainsIgnoreCase(query)
    }

    @GetMapping("/books/auditLog")
    fun getAuditLog() = AuditService.auditLogs


}