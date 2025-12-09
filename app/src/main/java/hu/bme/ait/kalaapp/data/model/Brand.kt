package hu.bme.ait.kalaapp.data.model

import com.google.firebase.firestore.DocumentId

data class Brand(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val location: String = "",
    val bio: String = "",
    val websiteUrl: String = "",
    val logoUrl: String = "",
    val imageUrl: String = "",
    val categories: List<String> = emptyList(),
    val values: List<String> = emptyList(),
)