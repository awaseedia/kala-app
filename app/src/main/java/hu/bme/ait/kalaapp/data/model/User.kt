package hu.bme.ait.kalaapp.data.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val savedProductIds: List<String> = emptyList()
)