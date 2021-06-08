package id.riverflows.snapcrime.ui.upload

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.databinding.ActivityUploadBinding
import id.riverflows.snapcrime.ui.custom.SingleTextInputDialog
import id.riverflows.snapcrime.util.UtilConstants.DATE_FORMAT
import id.riverflows.snapcrime.util.UtilConstants.MIME_TYPE_IMAGE
import id.riverflows.snapcrime.util.UtilConstants.UPLOAD_IMAGES_MAX
import id.riverflows.snapcrime.util.UtilSnackBar.getMessageFromErrorCode
import id.riverflows.snapcrime.util.UtilSnackBar.showIndeterminateSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity(), SingleTextInputDialog.OnSubmitSingleTextInputDialog {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: UploadViewModel
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var locationInputDialog: SingleTextInputDialog
    private var date = ""
    private var location = ""
    private val imagesUri = mutableListOf<Uri?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatePickerDialog()
        initLocationDialog()
        setupView()
        obtainViewModel()
        observeViewModel()
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
            showLocationDialog()
        }
        binding.btnAddImage.setOnClickListener {
            pickImagesIntent()
        }
        binding.btnCreateReport.setOnClickListener {
            setEnableCreateReportButton(false)
            showUploadProgressBar(true)
            uploadData()
            lifecycleScope.launch(Dispatchers.IO){
                delay(4000)
                showUploadProgressBar(false)
                setEnableCreateReportButton(true)
                val action = getString(R.string.action_ok)
                val message = getMessageFromErrorCode(applicationContext, 200)
                showIndeterminateSnackBar(binding.root, action, message)
            }
        }
    }

    private fun showUploadProgressBar(isLoading: Boolean){
        binding.progressUpload.visibility = if(isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun checkIsFormValid(){
        if(isFormValid()) setEnableCreateReportButton(true)
    }

    private fun obtainViewModel(){
        viewModel = ViewModelProvider(this)[UploadViewModel::class.java]
    }

    private fun observeViewModel(){
        viewModel.uploadResponse.observe(this){
            Timber.d(it)
        }
    }

    private fun uploadData(){
        viewModel.uploadData()
    }

    private fun initDatePickerDialog(){
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val style = android.R.style.Theme_Material_Light_Dialog_Alert
        datePickerDialog = DatePickerDialog(this, style, datePickerOnSetListener, year, month, day)
    }

    private fun showLocationDialog(){
        locationInputDialog.show(supportFragmentManager, LOCATION_DIALOG_TAG)
    }

    private fun initLocationDialog(){
        locationInputDialog = SingleTextInputDialog()
        locationInputDialog.setOnSubmitSingleTextInputDialog(this)
    }

    private val datePickerOnSetListener = DatePickerDialog.OnDateSetListener {
            _, year, month, dayOfMonth ->
        val monthName = DateFormatSymbols(Locale.getDefault()).months[month]
        val dateReadFormat = "$dayOfMonth $monthName, $year"
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
        date = dateFormat.format(calendar.time)
        binding.tvDate.text = dateReadFormat
        checkIsFormValid()
    }

    private fun showDatePickerDialog(){
        datePickerDialog.show()
        datePickerDialog.apply {
            getButton(DatePickerDialog.BUTTON_NEGATIVE).apply {
                setText(R.string.action_cancel)
                setTextColor(resources.getColor(R.color.black, null))
            }
            getButton(DatePickerDialog.BUTTON_POSITIVE).apply {
                setText(R.string.action_choose)
                setTextColor(resources.getColor(R.color.black, null))
            }
        }
    }

    private fun isFormValid() = date.isNotBlank() && location.isNotBlank() && imagesUri.isNotEmpty()


    private fun setEnableCreateReportButton(isActive: Boolean){
        with(binding.btnCreateReport){
            isEnabled = isActive
            isClickable = isActive
        }
    }

    private val pickImagesResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.let {
                Timber.d(it.toString())
                if(it.clipData != null){
                    //Multiple images chosen
                    it.clipData?.let { clipData ->
                        if(imagesUri.size + clipData.itemCount <= UPLOAD_IMAGES_MAX){
                            for(i in 0 until clipData.itemCount){
                                val imageUri = clipData.getItemAt(i).uri
                                imagesUri.add(imageUri)
                            }
                        }else{
                            val action = getString(R.string.action_ok)
                            val message = getString(R.string.error_upload_limit)
                            showIndeterminateSnackBar(binding.root, action, message)
                        }
                    }
                }else{
                    //Single image chosen
                    if(imagesUri.size + 1 <= UPLOAD_IMAGES_MAX){
                        val imageUri = it.data
                        imagesUri.add(imageUri)
                    }else{
                        val action = getString(R.string.action_ok)
                        val message = getString(R.string.error_upload_limit)
                        showIndeterminateSnackBar(binding.root, action, message)
                    }
                }
                inflateImagesPreview(imagesUri)
            }
            checkIsFormValid()
        }
    }

    private fun pickImagesIntent(){
        val intent = Intent().apply {
            type = MIME_TYPE_IMAGE
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        pickImagesResultLauncher.launch(intent)
    }

    private fun inflateImagesPreview(list: List<Uri?>){
        binding.containerImagesPreview.removeAllViews()
        for(i in list.indices){
            with(binding.containerImagesPreview){
                val view = layoutInflater.inflate(R.layout.item_single_image_preview, this, false) as ImageView
                Glide.with(this)
                    .load(list[i])
                    .into(view)
                addView(view, i)
            }
        }
    }

    override fun onSubmitDialog(data: String) {
        location = data
        binding.tvLocation.text = location
        checkIsFormValid()
    }

    companion object{
        const val LOCATION_DIALOG_TAG = "LOCATION DIALOG"
    }
}