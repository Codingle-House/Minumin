package id.co.minumin.presentation.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.co.minumin.R
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.AchievementDto
import id.co.minumin.data.dto.AchievementStatusDto.Done
import id.co.minumin.data.dto.AchievementStatusDto.None
import id.co.minumin.databinding.ItemRecyclerviewAchievementBinding
import id.co.minumin.presentation.home.adapter.AchievementAdapter.ItemViewHolder

class AchievementAdapter(
    private val context: Context,
    private val diffCallback: DiffCallback
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val dataSet: MutableList<AchievementDto> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemRecyclerviewAchievementBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(dataSet[holder.adapterPosition])
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(newDataSet: List<AchievementDto>) {
        calculateDiff(newDataSet)
    }

    private fun calculateDiff(newDataSet: List<AchievementDto>) {
        diffCallback.setList(dataSet, newDataSet)
        val result = DiffUtil.calculateDiff(diffCallback)
        with(dataSet) {
            clear()
            addAll(newDataSet)
        }
        result.dispatchUpdatesTo(this)
    }


    inner class ItemViewHolder(private val binding: ItemRecyclerviewAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindView(data: AchievementDto) = with(binding) {
            if (data.status is None) {
                achievementImageviewIcon.setImageDrawable(null)
            } else {
                achievementImageviewIcon.setImageDrawable(
                    context.getDrawableCompat(
                        when (data.status) {
                            Done -> R.drawable.general_ic_coin_done
                            else -> R.drawable.general_ic_coin_fail
                        }
                    )
                )
            }
            achievementTextviewDay.text = data.day
        }
    }
}