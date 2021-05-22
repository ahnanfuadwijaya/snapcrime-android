package id.riverflows.snapcrime.ui.upload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.databinding.ActivityUploadBinding
import timber.log.Timber

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: UploadViewModel
    private var isDateValid = false
    private var date = ""
    private var isLocationValid = false
    private var location = ""
    private var isUploadValid = false
    private val imagesPath = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        obtainViewModel()
        observeViewModel()
        requestData()
    }

    private fun setupView(){
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setEnableCreateReportButton(false)
        setContentView(binding.root)
        binding.appbar.tvRootTitle.text = getString(R.string.title_upload)
        binding.appbar.btnBack.setOnClickListener { onBackPressed() }
        binding.tvDateField.setOnClickListener {
            showDatePickerDialog()
        }
        binding.tvLocationField.setOnClickListener {
            showTextInputDialog()
        }
        binding.btnAddImage.setOnClickListener {
            Timber.d("AddImageButton clicked")
            //TODO select image(s) from gallery, check validation
            //if all inputs are valid, set CreateReportButton enable and clickable
            if(isFormValid()) setEnableCreateReportButton(true)
        }
        binding.btnCreateReport.setOnClickListener {
            Timber.d("CreateReportButton clicked")
            //TODO create report
        }
    }

    private fun obtainViewModel(){
        viewModel = ViewModelProvider(this)[UploadViewModel::class.java]
    }

    private fun observeViewModel(){
        viewModel.dummyData.observe(this){
            Timber.d(it)
        }
    }

    private fun requestData(){
        viewModel.getDummyData()
    }

    private fun showDatePickerDialog(){
        Timber.d("DatePickerDialog clicked")
        //TODO show date picker dialog
    }

    private fun showTextInputDialog(){
        Timber.d("TextInputDialog shown")
        //TODO show text input dialog
    }

    private fun isFormValid(): Boolean{
        return isDateValid && isLocationValid && isUploadValid
    }

    private fun setEnableCreateReportButton(isActive: Boolean){
        with(binding.btnCreateReport){
            isEnabled = isActive
            isClickable = isActive
        }
    }
}