package com.bookinventory.apibookstore.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document


@Document data class Book(@Id val id:String?,
                          var title:String?,
                          var authors:List<String>?,
                          var description:String?,
                          var price: Double,
                          var quantity: Int,
                          var imageLinks: ImageUrl?) {
}


data class ImageUrl(val smallThumbnail:String?,
                    val thumbnail:String?
                    )

data class Item(val volumeInfo: Book?)

data class GooleBook(val items:List<Item>?)