package id.riverflows.snapcrime.util

import id.riverflows.snapcrime.data.DetailCase

object UtilDummyData {
    private val detailCase: DetailCase = DetailCase(
        1L,
        "21 Mei 2021",
        "Jl. Medan Merdeka Timur No. 1",
        "Pistol",
        "Selesai",
        "sample_image"
    )
    fun getDetailCase() = detailCase
}