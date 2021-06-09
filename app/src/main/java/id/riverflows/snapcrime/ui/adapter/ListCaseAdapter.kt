package id.riverflows.snapcrime.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import id.riverflows.snapcrime.R
import id.riverflows.snapcrime.data.Case
import id.riverflows.snapcrime.data.DetailCase
import id.riverflows.snapcrime.util.UtilConstants
import kotlinx.android.synthetic.main.item_row.view.*

class ListCaseAdapter(private val listCase: List<DetailCase>) : RecyclerView.Adapter<ListCaseAdapter.ListViewHolder>() {
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
        fun bind(case: DetailCase) {
            val context = itemView.context
            val imageResource = context.resources.getIdentifier(case.imageUrl,
                UtilConstants.DEF_TYPE_RAW, context.packageName)
            with(itemView){
                Glide.with(context)
                    .load(imageResource)
                    .apply(RequestOptions()
                        .placeholder(R.drawable.ic_loading)
                        .error(R.drawable.ic_broken_image))
                    .into(iivPhoto)
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