package hu.bme.ait.kalaapp.data.model

data class Brand(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val location: String = "",
    val values: List<String> = emptyList(), // e.g., ["Sustainable", "Luxury"]
    val websiteUrl: String = "",
    val logoUrl: String = "",
    val imageUrl: String = ""
)