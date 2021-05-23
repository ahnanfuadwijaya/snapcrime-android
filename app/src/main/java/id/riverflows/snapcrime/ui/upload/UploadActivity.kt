package id.riverflows.snapcrime.ui.upload

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.databinding.ActivityUploadBinding
import id.riverflows.snapcrime.util.UtilConstants.DATE_FORMAT
import id.riverflows.snapcrime.util.UtilConstants.MIME_TYPE_IMAGE
import id.riverflows.snapcrime.util.UtilConstants.UPLOAD_IMAGES_MAX
import timber.log.Timber
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var viewModel: UploadViewModel
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var locationInputDialog: AlertDialog
    private var date = ""
    private var location = ""
    private val imagesUri = mutableListOf<Uri?>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDatePickerDialog()
        initLocationInputDialog()
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
            showLocationTextInputDialog()
        }
        binding.btnAddImage.setOnClickListener {
            pickImagesIntent()
        }
        binding.btnCreateReport.setOnClickListener {
            Timber.d("CreateReportButton clicked")
            uploadData()
        }
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

    @SuppressLint("InflateParams")
    private fun initLocationInputDialog(){
        val builder = AlertDialog.Builder(
            this, R.style.MaterialAlertDialog_MaterialComponents
        ).apply {
            val contentView = layoutInflater.inflate(R.layout.dialog_text_input, null)
            val edtInputText = contentView.findViewById<EditText>(R.id.edt_input_text_dialog)
            edtInputText.hint = getString(R.string.hint_enter_location)
            setView(contentView)
            setPositiveButton(getString(R.string.action_ok)){ dialog, _ ->
                location = edtInputText.text.toString()
                binding.tvLocation.text = location
                dialog.dismiss()
                checkIsFormValid()
            }
            setNeutralButton(getString(R.string.action_cancel)){ dialog, _ ->
                dialog.dismiss()
            }
        }
        locationInputDialog = builder.create()
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

    private fun showLocationTextInputDialog(){
        Timber.d("TextInputDialog shown")
        val width = resources.displayMetrics.widthPixels
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
        locationInputDialog.show()
        locationInputDialog.window?.setLayout(width, height)
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
                            //TODO show upload images limit reached snack bar
                        }
                    }
                }else{
                    //Single image chosen
                    if(imagesUri.size + 1 <= UPLOAD_IMAGES_MAX){
                        val imageUri = it.data
                        imagesUri.add(imageUri)
                    }else{
                        //TODO show upload images limit reached snack bar
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
}