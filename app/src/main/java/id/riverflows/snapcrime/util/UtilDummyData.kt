@file:Suppress("SpellCheckingInspection")

package id.riverflows.snapcrime.util

import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.data.DetailCase
import id.riverflows.snapcrime.data.response.CaseListResponse
import id.riverflows.snapcrime.data.response.DetailCaseResponse
import id.riverflows.snapcrime.data.response.LoginResponse
import id.riverflows.snapcrime.data.response.UploadCaseResponse

object UtilDummyData {
    const val USERNAME = "bangkit"
    const val PASSWORD = "12345"
    private val casesList: List<Case> = listOf(
        Case(
            1L,
            "06  2021",
            "Jl. Ahmad Yani",
            "Pistol",
            "sample_image"
        ),
        Case(
            2L,
            "21 Mei 2021",
            "Jl. Stasiun Wonokromo",
            "Pistol",
            "sample_image"
        ),
        Case(
            3L,
            "21 Mei 2021",
            "Jl. Mastrip",
            "Pistol",
            "sample_image"
        ),
        Case(
            4L,
            "21 Mei 2021",
            "Jl. Gunungsari",
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
            "06 Juni 2021",
            "Jl. Ahmad Yani",
            "Pistol",
            "Selesai",
            "handgun_01"
        ),
        DetailCase(
            2L,
            "06 Juni 2021",
            "Jl. Stasiun Wonokromo",
            "Pistol",
            "Selesai",
            "handgun_02"
        ),
        DetailCase(
            3L,
            "04 Juni 2021",
            "Jl. Mastrip",
            "Pistol",
            "Selesai",
            "handgun_03"
        ),
        DetailCase(
            4L,
            "29 Mei 2021",
            "Jl. Hayam Wuruk",
            "Pistol",
            "Selesai",
            "handgun_04"
        ),
        DetailCase(
            5L,
            "27 Mei 2021",
            "Jl. Pulo Wonokromo",
            "Pistol",
            "Selesai",
            "handgun_05"
        ),
        DetailCase(
            6L,
            "21 Mei 2021",
            "Jl. Mayjen Sungkono",
            "Pistol",
            "Selesai",
            "handgun_06"
        ),
        DetailCase(
            7L,
            "18 Mei 2021",
            "Jl. Ngagel Jaya Selatan",
            "Pisau",
            "Selesai",
            "knife_01"
        ),
        DetailCase(
            8L,
            "15 Mei 2021",
            "Jl. Kupang Gunung",
            "Pisau",
            "Selesai",
            "knife_02"
        ),
        DetailCase(
            9L,
            "14 Mei 2021",
            "Jl. Mayjen HR. Mohammad",
            "Pisau",
            "Selesai",
            "knife_03"
        ),
        DetailCase(
            10L,
            "12 Mei 2021",
            "Jl. Wisma Lidah Kulon",
            "Pisau",
            "Selesai",
            "knife_04"
        ),
        DetailCase(
            11L,
            "09 Mei 2021",
            "Jl. Wonorejo Rungkut",
            "Pisau",
            "Selesai",
            "knife_05"
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
    fun getDetailCasesList(): List<DetailCase> = detailCasesList
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