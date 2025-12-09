package hu.bme.ait.kalaapp.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val savedProductIds: List<String> = emptyList()
)