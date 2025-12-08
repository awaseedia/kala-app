package hu.bme.ait.kalaapp.data.model

data class Brand(
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val bio: String = "",
    val websiteUrl: String = "",
    val logoUrl: String = "",
    val ethicTags: List<String> = emptyList(),
    val values: List<String> = emptyList(),
)