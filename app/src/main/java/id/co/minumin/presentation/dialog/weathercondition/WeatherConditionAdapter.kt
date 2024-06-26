package id.co.minumin.presentation.dialog.weathercondition

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import id.co.minumin.R
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.dto.WeatherConditionDto.HOT
import id.co.minumin.data.dto.WeatherConditionDto.NORMAL
import id.co.minumin.data.dto.WeatherConditionDto.WARM
import id.co.minumin.data.dto.WeatherConditionDto.WINTER
import id.co.minumin.databinding.ItemRecyclerviewWeatherconditionBinding
import id.co.minumin.presentation.dialog.weathercondition.WeatherConditionAdapter.ItemViewHolder

class WeatherConditionAdapter(
    private val context: Context,
    private val diffCallback: DiffCallback,
    private val itemListener: (WeatherConditionDto) -> Unit = { _ -> run {} }
) : RecyclerView.Adapter<ItemViewHolder>() {

    private val dataSet: MutableList<WeatherConditionDto> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemRecyclerviewWeatherconditionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(dataSet[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int = dataSet.size

    fun setData(newDataSet: List<WeatherConditionDto>) = calculateDiff(newDataSet)

    private fun calculateDiff(newDataSet: List<WeatherConditionDto>) {
        diffCallback.setList(dataSet, newDataSet)
        val result = DiffUtil.calculateDiff(diffCallback)
        with(dataSet) {
            clear()
            addAll(newDataSet)
        }
        result.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(private val binding: ItemRecyclerviewWeatherconditionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bindView(data: WeatherConditionDto) = with(binding) {
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

        private fun getTextAndImage(data: WeatherConditionDto) = when (data) {
            NORMAL -> Pair(R.string.dialog_text_weather_normal, R.drawable.general_ic_weathernormal)
            WINTER -> Pair(R.string.dialog_text_weather_winter, R.drawable.general_ic_weatherwinter)
            WARM -> Pair(R.string.dialog_text_weather_warm, R.drawable.general_ic_weatherwarm)
            HOT -> Pair(R.string.dialog_text_weather_hot, R.drawable.general_ic_weatherhot)
        }
    }
}