package id.riverflows.snapcrime.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.DetailCase
import id.riverflows.snapcrime.databinding.ActivityDetailBinding
import id.riverflows.snapcrime.util.UtilConstants
import id.riverflows.snapcrime.util.UtilConstants.BOTT_SHEET_PEEK_HEIGHT
import id.riverflows.snapcrime.util.UtilConstants.DEF_TYPE_RAW
import id.riverflows.snapcrime.util.UtilSnackBar.showIndeterminateSnackBar
import kotlinx.android.synthetic.main.item_row.*

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        obtainViewModel()
        observeViewModel()
        requestData()
    }

    private fun setupView(){
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        BottomSheetBehavior.from(binding.bottomSheet.root).apply {
            peekHeight = BOTT_SHEET_PEEK_HEIGHT
            state = BottomSheetBehavior.STATE_COLLAPSED
        }
        binding.appbar.btnBack.setOnClickListener { onBackPressed() }
    }

    private fun obtainViewModel(){
        viewModel = ViewModelProvider(this)[DetailViewModel::class.java]
    }

    private fun observeViewModel(){
        viewModel.detailCaseResponse.observe(this){
            if(it.data != null){
                bindDataWithView(it.data)
            }else{
                val action = getString(R.string.action_ok)
                val message = getString(R.string.error_no_data)
                showIndeterminateSnackBar(binding.root, action, message)
            }
        }
    }

    private fun requestData(){
        val id = intent.getLongExtra(UtilConstants.EXTRA_CASE_ID, 1L)
        viewModel.getDetailCaseResponse(id)
    }

    private fun bindDataWithView(data: DetailCase){
        with(binding){
            val imageResource = resources.getIdentifier(data.imageUrl, DEF_TYPE_RAW, packageName)
            Glide.with(this@DetailActivity)
                .load(imageResource)
                .into(ivDetail)
            bottomSheet.tvBottomSheetTitle.text = getString(R.string.title_detail_report)
            bottomSheet.tvDate.text = data.date
            bottomSheet.tvLocation.text = data.location
            bottomSheet.tvLabel.text = data.label
            bottomSheet.tvStatus.text = data.status
        }
    }
}