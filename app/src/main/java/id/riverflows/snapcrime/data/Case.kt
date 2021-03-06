package id.riverflows.snapcrime.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Case(
    val id: Long,
    val date: String,
    val location: String,
    val label: String,
    val imageUrl: String
) : Parcelable
