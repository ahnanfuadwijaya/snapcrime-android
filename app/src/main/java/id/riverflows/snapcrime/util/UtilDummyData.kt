@file:Suppress("SpellCheckingInspection")

package id.riverflows.snapcrime.util

import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.data.DetailCase
import id.riverflows.snapcrime.data.response.CaseListResponse
import id.riverflows.snapcrime.data.response.DetailCaseResponse
import id.riverflows.snapcrime.data.response.LoginResponse
import id.riverflows.snapcrime.data.response.UploadCaseResponse

object UtilDummyData {
    private val casesList: List<Case> = listOf(
        Case(
            1L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            2L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            3L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            4L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            5L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            6L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            7L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            8L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            9L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            10L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            11L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        ),
        Case(
            12L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "sample_image"
        )
    )
    private val detailCasesList: List<DetailCase> = listOf(
        DetailCase(
            1L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            2L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            3L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            4L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            5L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            6L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            7L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            8L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            9L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            10L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            11L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        ),
        DetailCase(
            12L,
            "21 Mei 2021",
            "Jl. Medan Merdeka Timur No. 1",
            "Pistol",
            "Selesai",
            "sample_image"
        )
    )
    fun getAuthLoginResponse(username: String, password: String): LoginResponse{
        return if(username =="user" && password == "user") {
            LoginResponse(
                200,
                true,
                "Login Success",
                "abcdefg123!@#"
            )
        }
        else {
            LoginResponse(
                401,
                false,
                "Incorrect username or password",
                null
            )
        }
    }
    fun getCasesListResponse() = CaseListResponse(
        200,
        true,
        "Request Success",
        casesList
    )
    fun getDetailCaseResponse(id: Long): DetailCaseResponse{
        val data = detailCasesList.firstOrNull { it.id == id }
        return data?.let {
             DetailCaseResponse(
                200,
                true,
                "Request Success",
                it
            )
        } ?: DetailCaseResponse(
            200,
            false,
            "Data Not Found",
            null
        )
    }
    fun getUploadCaseResponse() = UploadCaseResponse(
        200,
        true,
        "Data Uploaded Successfully"
    )
}