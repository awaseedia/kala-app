package hu.bme.ait.kalaapp.data.model

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId
    val id: String = "",
    val brandId: String = "",
    val brandName: String = "",
    val name: String = "",
    val bio: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val categories: List<String> = emptyList(),
    val imageUrl: String = "",
    val productUrl: String = "",
    val isFeatured: Boolean = false
)