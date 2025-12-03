package hu.bme.ait.kalaapp.data.model

data class Product(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "USD",
    val category: String = "", // e.g., "casual", "evening", "abaya", "activewear"
    val brandId: String = "",
    val brandName: String = "",
    val imageUrl: String = "",
    val productUrl: String = "",
    val isFeatured: Boolean = false
)