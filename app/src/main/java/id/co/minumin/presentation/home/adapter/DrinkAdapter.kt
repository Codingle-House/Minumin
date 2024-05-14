package id.co.minumin.presentation.home.adapter

import android.content.Context
import android.graphics.Typeface
import android.graphics.Typeface.BOLD
import android.text.Spannable
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil.calculateDiff
import androidx.recyclerview.widget.RecyclerView
import id.co.minumin.R
import id.co.minumin.core.CustomTypefaceSpan
import id.co.minumin.core.DiffCallback
import id.co.minumin.core.ext.getDrawableCompat
import id.co.minumin.data.dto.CupSelectionDto.CUP_100
import id.co.minumin.data.dto.CupSelectionDto.CUP_150
import id.co.minumin.data.dto.CupSelectionDto.CUP_200
import id.co.minumin.data.dto.CupSelectionDto.CUP_300
import id.co.minumin.data.dto.CupSelectionDto.CUP_400
import id.co.minumin.data.dto.DrinkDto
import id.co.minumin.databinding.ItemRecyclerviewDrinkBinding
import id.co.minumin.presentation.home.adapter.DrinkAdapter.MenuAction.Delete
import id.co.minumin.presentation.home.adapter.DrinkAdapter.MenuAction.Edit

/**
 * Created by pertadima on 11,February,2021
 */

class DrinkAdapter(
    private val context: Context,
    private val diffCallback: DiffCallback,
    private val itemListener: (MenuAction) -> Unit = { _ -> kotlin.run {} }
) : RecyclerView.Adapter<DrinkAdapter.ItemViewHolder>() {

    private val dataSet: MutableList<DrinkDto> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemBinding = ItemRecyclerviewDrinkBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ItemViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bindView(dataSet[holder.bindingAdapterPosition])
    }

    override fun getItemCount(): Int = dataSet.size

    override fun getItemId(position: Int): Long = dataSet[position].id.hashCode().toLong()

    fun setData(newDataSet: List<DrinkDto>) {
        calculateDiff(newDataSet)
    }

    private fun calculateDiff(newDataSet: List<DrinkDto>) {
        diffCallback.setList(dataSet, newDataSet)
        val result = calculateDiff(diffCallback)
        with(dataSet) {
            clear()
            addAll(newDataSet)
        }
        result.dispatchUpdatesTo(this)
    }

    inner class ItemViewHolder(private val binding: ItemRecyclerviewDrinkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(data: DrinkDto) {
            binding.drinkTextviewDate.text =
                context.getString(R.string.general_text_time, data.time)

            val consumption =
                data.consumption.toString() + context.getString(R.string.general_text_ml)
            binding.drinkImageviewDescription.styleContentTitle(consumption)
            val icon = when (data.consumption) {
                CUP_100.capacity -> R.drawable.general_ic_cup_100
                CUP_150.capacity -> R.drawable.general_ic_cup_150
                CUP_200.capacity -> R.drawable.general_ic_cup_200
                CUP_300.capacity -> R.drawable.general_ic_cup_300
                CUP_400.capacity -> R.drawable.general_ic_cup_400
                else -> R.drawable.general_ic_cup_custom
            }
            binding.drinkImageviewCup.setImageDrawable(context.getDrawableCompat(icon))
            binding.drinkImageviewMenu.setOnClickListener { showPopUp(binding, data) }
        }

        private fun showPopUp(binding: ItemRecyclerviewDrinkBinding, data: DrinkDto) {
            val popup = PopupMenu(context, binding.drinkImageviewMenu)
            popup.inflate(R.menu.drink_nav_menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.drink_menu_delete -> {
                        itemListener.invoke(Delete(data))
                        true
                    }

                    R.id.drink_menu_edit -> {
                        itemListener.invoke(Edit(data))
                        true
                    }

                    else -> false
                }
            }
            popup.show()
        }


        private fun TextView.styleContentTitle(contextText: String) {
            val firstWord = SpannableString(
                context.getString(R.string.home_text_item_drink)
                    .substringBefore(CONTENT_STYLE_DELIMITED)
            )
            text = firstWord
            val typeface = Typeface.create(
                ResourcesCompat.getFont(context, R.font.montserrat_bold),
                BOLD
            )

            val notesCount = SpannableString(contextText).apply {
                setSpan(
                    CustomTypefaceSpan("", typeface),
                    0,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            append(notesCount)
        }
    }

    sealed class MenuAction {
        data class Delete(val data: DrinkDto) : MenuAction()
        data class Edit(val data: DrinkDto) : MenuAction()
    }

    companion object {
        private const val CONTENT_STYLE_DELIMITED = "%s"
    }
}