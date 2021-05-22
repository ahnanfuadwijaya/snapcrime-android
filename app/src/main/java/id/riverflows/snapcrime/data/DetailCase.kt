package id.riverflows.snapcrime.data

data class DetailCase(
    val id: Long,
    val date: String,
    val location: String,
    val label: String,
    val status: String,
    val imageUrl: String
)