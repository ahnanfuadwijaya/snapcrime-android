package id.riverflows.snapcrime.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.ui.adapter.ListCaseAdapter
import id.riverflows.snapcrime.ui.detail.DetailActivity
import id.riverflows.snapcrime.ui.upload.UploadActivity
import id.riverflows.snapcrime.util.UtilConstants
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity(), ListCaseAdapter.OnItemClickCallback {
    private val list = ArrayList<Case>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        fab_create_report.setOnClickListener { moveToUpload() }
        rvCase.setHasFixedSize(true)
        list.addAll(getListCasees())
        showRecyclerList()
    }

    fun getListCasees(): ArrayList<Case> {
        val datadate = resources.getStringArray(R.array.data_date)
        val datalocation = resources.getStringArray(R.array.data_location)
        val datalable = resources.getStringArray(R.array.data_lable)
        val listCase = ArrayList<Case>()
        for (position in datadate.indices) {
            val Case = Case(
                (position+1).toLong(),
                datadate[position],
                datalocation[position],
                datalable[position],
                "sample_image"
            )
            listCase.add(Case)
        }
        return listCase
    }

    private fun showRecyclerList() {
        rvCase.layoutManager = LinearLayoutManager(this)
        val listCaseAdapter = ListCaseAdapter(list)
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