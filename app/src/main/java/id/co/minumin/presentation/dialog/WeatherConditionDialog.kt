package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.data.dto.WeatherConditionDto
import id.co.minumin.data.dto.WeatherConditionDto.HOT
import id.co.minumin.data.dto.WeatherConditionDto.NORMAL
import id.co.minumin.data.dto.WeatherConditionDto.WARM
import id.co.minumin.data.dto.WeatherConditionDto.WINTER
import id.co.minumin.databinding.DialogWeatherConditionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class WeatherConditionDialog(
    context: Context
) : MinuminDialog<DialogWeatherConditionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogWeatherConditionBinding
        get() = DialogWeatherConditionBinding::inflate


    private var action: (WeatherConditionDto) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        weatherRelativelayoutHot.setOnClickListener {
            action.invoke(HOT)
            dismiss()
        }

        weatherRelativelayoutWarm.setOnClickListener {
            action.invoke(WARM)
            dismiss()
        }

        weatherRelativelayoutNormal.setOnClickListener {
            action.invoke(NORMAL)
            dismiss()
        }

        weatherRelativelayoutWinter.setOnClickListener {
            action.invoke(WINTER)
            dismiss()
        }
    }

    fun setListener(action: (WeatherConditionDto) -> Unit): WeatherConditionDialog {
        this.action = action
        return this
    }

    companion object {
        fun newInstance(context: Context) = WeatherConditionDialog(context)
    }
}