package id.riverflows.snapcrime.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.Case
import kotlinx.android.synthetic.main.item_row.view.*

class ListCaseAdapter(private val listCase: ArrayList<Case>) : RecyclerView.Adapter<ListCaseAdapter.ListViewHolder>() {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ListViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_row, viewGroup, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(listCase[position])
    }

    override fun getItemCount(): Int = listCase.size

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(case: Case) {
            with(itemView){
                tvDate.text = case.date
                tvLocation.text = case.location
                tvLable.text = case.label
            }
            itemView.setOnClickListener { onItemClickCallback.onItemClicked(case.id) }
        }
    }

    interface OnItemClickCallback{
        fun onItemClicked(id: Long)
    }
}