package id.riverflows.snapcrime.util

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import id.riverflows.snapcrime.R

object UtilSnackBar {

    fun showIndeterminateSnackBar(view: View, action: String, message: String){
        val snackBar = Snackbar.make(view, message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction(action){
            snackBar.dismiss()
        }.show()
    }

    fun getMessageFromErrorCode(context: Context, code: Int?): String{
        return when(code){
            in 100..199 -> context.getString(R.string.error_1xx)
            in 200..299 -> context.getString(R.string.error_2xx)
            in 300..399 -> context.getString(R.string.error_3xx)
            in 400..499 -> context.getString(R.string.error_4xx)
            in 500..599 -> context.getString(R.string.error_5xx)
            null -> context.getString(R.string.error_network)
            else -> context.getString(R.string.error_unknown)
        }
    }
}