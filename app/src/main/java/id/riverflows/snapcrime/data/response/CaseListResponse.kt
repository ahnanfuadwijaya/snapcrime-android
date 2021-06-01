package id.riverflows.snapcrime.data.response

import id.riverflows.snapcrime.data.Case

data class CaseListResponse(
    val code: Int,
    val status: Boolean,
    val message: String,
    val data: List<Case>
)