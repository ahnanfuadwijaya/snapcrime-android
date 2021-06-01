package id.riverflows.snapcrime.data.response

import id.riverflows.snapcrime.data.DetailCase

data class DetailCaseResponse(
    val code: Int,
    val status: Boolean,
    val message: String,
    val data: DetailCase?
)