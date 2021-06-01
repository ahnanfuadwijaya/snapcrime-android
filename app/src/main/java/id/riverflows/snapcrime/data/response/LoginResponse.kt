package id.riverflows.snapcrime.data.response

data class LoginResponse (
    val code: Int,
    val status: Boolean,
    val message: String,
    val token: String
)