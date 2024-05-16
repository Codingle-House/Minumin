package id.co.minumin.presentation.dialog.weathercondition

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.minumin.core.DiffCallback
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.databinding.DialogWeatherConditionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class WeatherConditionDialog(
    context: Context,
    diffCallback: DiffCallback,
) : MinuminDialog<DialogWeatherConditionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogWeatherConditionBinding
        get() = DialogWeatherConditionBinding::inflate

    private val weatherAdapter by lazy {
        WeatherConditionAdapter(context, diffCallback, ::onWeatherConditionClick).apply {
            setData(WeatherConditionDto.entries.toTypedArray().toList())
        }
    }

    private var action: (WeatherConditionDto) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        with(binding) {
            weatherRv.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = weatherAdapter
            }
        }
    }

    private fun onWeatherConditionClick(data: WeatherConditionDto) {
        action.invoke(data)
        dismiss()
    }

    fun setListener(action: (WeatherConditionDto) -> Unit): WeatherConditionDialog {
        this.action = action
        return this
    }

    companion object {
        fun newInstance(context: Context, diffCallback: DiffCallback) =
            WeatherConditionDialog(context, diffCallback)
    }
}