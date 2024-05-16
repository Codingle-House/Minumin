package id.co.minumin.presentation.dialog.physicalactivities

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import id.co.minumin.R
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.PhysicalActivitiesDto.ACTIVE
import id.co.minumin.data.dto.PhysicalActivitiesDto.NORMAL
import id.co.minumin.data.dto.PhysicalActivitiesDto.VERY_ACTIVE
import id.co.minumin.databinding.ItemRecyclerviewPhysicalactivitiesBinding
import id.co.minumin.presentation.dialog.physicalactivities.PhysicalActivitiesAdapter.ItemViewHolder

/**
 * Created by pertadima on 11,February,2021
 */

class PhysicalActivitiesAdapter(
    private val context: Context,
    private val diffCallback: DiffCallback,
    private val itemListener: (PhysicalActivitiesDto) -> Unit = { _ -> run {} }
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val dataSet: MutableList<PhysicalActivitiesDto> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemRecyclerviewPhysicalactivitiesBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(dataSet[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(newDataSet: List<PhysicalActivitiesDto>) = calculateDiff(newDataSet)

    private fun calculateDiff(newDataSet: List<PhysicalActivitiesDto>) {
        diffCallback.setList(dataSet, newDataSet)
        val result = calculateDiff(diffCallback)
        with(dataSet) {
            clear()
            addAll(newDataSet)
        }
        result.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(private val binding: ItemRecyclerviewPhysicalactivitiesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindView(data: PhysicalActivitiesDto) = with(binding) {
            itemRlParent.setOnClickListener { itemListener.invoke(data) }
            line.isGone = bindingAdapterPosition == dataSet.size.dec()
            itemTvMl.text = "+${data.consumption}${context.getString(R.string.general_text_ml)}"
            val (title, drawable) = getTextAndImage(data)
            itemTvText.apply {
                text = context.getString(title)
                setCompoundDrawablesWithIntrinsicBounds(
                    context.getDrawableCompat(drawable),
                    null,
                    null,
                    null
                )
            }
        }

        private fun getTextAndImage(data: PhysicalActivitiesDto) = when (data) {
            NORMAL -> Pair(R.string.dialog_text_physical_normal, R.drawable.general_ic_bodynormal)
            ACTIVE -> Pair(R.string.dialog_text_physical_active, R.drawable.general_ic_bodyactive)
            VERY_ACTIVE -> Pair(
                R.string.dialog_text_physical_extraactive,
                R.drawable.general_ic_bodyextraactive
            )
        }
    }
}