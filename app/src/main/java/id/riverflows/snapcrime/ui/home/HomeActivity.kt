package id.riverflows.snapcrime.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.util.Util
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.ui.adapter.ListCaseAdapter
import id.riverflows.snapcrime.ui.detail.DetailActivity
import id.riverflows.snapcrime.ui.upload.UploadActivity
import id.riverflows.snapcrime.util.UtilConstants
import id.riverflows.snapcrime.util.UtilDummyData
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), ListCaseAdapter.OnItemClickCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        fab_create_report.setOnClickListener { moveToUpload() }
        rvCase.setHasFixedSize(true)
        showRecyclerList()
    }

    private fun showRecyclerList() {
        rvCase.layoutManager = LinearLayoutManager(this)
        val listCaseAdapter = ListCaseAdapter(UtilDummyData.getDetailCasesList())
        listCaseAdapter.setOnItemClickCallback(this)
        rvCase.adapter = listCaseAdapter
    }

    private fun moveToDetail(id: Long){
        startActivity(Intent(this, DetailActivity::class.java).apply {
            putExtra(UtilConstants.EXTRA_CASE_ID, id)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
    }

    private fun moveToUpload(){
        startActivity(Intent(this, UploadActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        })
    }

    override fun onItemClicked(id: Long) {
        moveToDetail(id)
    }
}