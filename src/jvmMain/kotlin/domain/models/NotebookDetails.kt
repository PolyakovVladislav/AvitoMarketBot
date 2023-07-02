package domain.models

data class NotebookDetails(
    val id: Long,
    val title: String,
    val price: Int,
    val details: String,
    val description: String
)
