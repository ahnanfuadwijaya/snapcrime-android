package id.riverflows.snapcrime.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.ui.adapter.ListCaseAdapter
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val list = ArrayList<Case>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

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
                datadate[position],
                datalocation[position],
                datalable[position]
            )
            listCase.add(Case)
        }
        return listCase
    }

    private fun showRecyclerList() {
        rvCase.layoutManager = LinearLayoutManager(this)
        val listCaseAdapter = ListCaseAdapter(list)
        rvCase.adapter = listCaseAdapter
    }
}