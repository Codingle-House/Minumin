package id.co.minumin.presentation.dialog.cupselection

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.co.minumin.R
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.CupSelectionDto.CUP_100
import id.co.minumin.data.dto.CupSelectionDto.CUP_150
import id.co.minumin.data.dto.CupSelectionDto.CUP_200
import id.co.minumin.data.dto.CupSelectionDto.CUP_300
import id.co.minumin.data.dto.CupSelectionDto.CUP_400
import id.co.minumin.data.dto.CupSelectionDto.CUP_CUSTOM
import id.co.minumin.databinding.ItemRecyclerviewCupselectionBinding

class CupSelectionAdapter(
    private val context: Context,
    private val diffCallback: DiffCallback,
    private val itemListener: (CupSelectionDto) -> Unit = { _ -> run {} }
) : RecyclerView.Adapter<CupSelectionAdapter.ItemViewHolder>() {

    private val dataSet: MutableList<CupSelectionDto> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemRecyclerviewCupselectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(dataSet[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(newDataSet: List<CupSelectionDto>) = calculateDiff(newDataSet)

    private fun calculateDiff(newDataSet: List<CupSelectionDto>) {
        diffCallback.setList(dataSet, newDataSet)
        val result = DiffUtil.calculateDiff(diffCallback)
        with(dataSet) {
            clear()
            addAll(newDataSet)
        }
        result.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(private val binding: ItemRecyclerviewCupselectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindView(data: CupSelectionDto) = with(binding) {
            val (capacity, image) = when (data) {
                CUP_100 -> Pair(
                    "${CUP_100.capacity} ${context.getString(R.string.general_text_ml)}",
                    R.drawable.general_ic_cupadd_100
                )

                CUP_150 -> Pair(
                    "${CUP_150.capacity} ${context.getString(R.string.general_text_ml)}",
                    R.drawable.general_ic_cupadd_150
                )

                CUP_200 -> Pair(
                    "${CUP_200.capacity} ${context.getString(R.string.general_text_ml)}",
                    R.drawable.general_ic_cupadd_200
                )

                CUP_300 -> Pair(
                    "${CUP_300.capacity} ${context.getString(R.string.general_text_ml)}",
                    R.drawable.general_ic_cupadd_300
                )

                CUP_400 -> Pair(
                    "${CUP_400.capacity} ${context.getString(R.string.general_text_ml)}",
                    R.drawable.general_ic_cupadd_400
                )

                CUP_CUSTOM -> Pair(
                    context.getString(R.string.general_text_custom),
                    R.drawable.general_ic_cupadd_custom
                )
            }
            cupselectionIvCapacity.text = capacity
            cupselectionIvCup.setImageDrawable(context.getDrawableCompat(image))
            cupParent.setOnClickListener { itemListener(data) }
        }
    }
}