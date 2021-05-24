package id.riverflows.snapcrime.ui.custom

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.res.ResourcesCompat
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.databinding.DialogTextInputBinding

class SingleTextInputDialog: AppCompatDialogFragment() {
    private lateinit var onSubmitSingleTextInputDialog: OnSubmitSingleTextInputDialog
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context)
        val binding = DialogTextInputBinding.inflate(layoutInflater)
        //val view = activity?.layoutInflater?.inflate(R.layout.dialog_text_input, null)
        builder.setView(binding.root)
            .setTitle(getString(R.string.title_location))
            .setPositiveButton(getString(R.string.action_ok)){ dialog, _ ->
                val data = binding.tfLocation.editText?.text.toString()
                onSubmitSingleTextInputDialog.onSubmitDialog(data)
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.action_cancel)){ dialog, _ ->
                dialog.dismiss()
            }
        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ResourcesCompat.getColor(resources, R.color.black, null))
        }
        return dialog
    }

    fun setOnSubmitSingleTextInputDialog(onSubmitSingleTextInputDialog: OnSubmitSingleTextInputDialog){
        this.onSubmitSingleTextInputDialog = onSubmitSingleTextInputDialog
    }

    interface OnSubmitSingleTextInputDialog{
        fun onSubmitDialog(data: String)
    }
}