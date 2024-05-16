package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogWaterConsumptionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 08,February,2021
 */

class WaterConsumptionDialog(
    context: Context,
    private var waterConsumption: Int
) : MinuminDialog<DialogWaterConsumptionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogWaterConsumptionBinding
        get() = DialogWaterConsumptionBinding::inflate

    private var action: (Int) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        registerFormWater.setText(waterConsumption.toString())
        registerButtonSubmit.setOnClickListener {
            with(binding.registerFormWater) {
                if (getFormText().isEmpty()) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            waterConsumption = registerFormWater.getFormText().toInt()
            with(registerFormWater) {
                if (waterConsumption == 0) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            if (waterConsumption == 0) return@setOnClickListener

            action.invoke(waterConsumption)
            dismiss()
        }
    }

    fun setListener(action: (Int) -> Unit): WaterConsumptionDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context, waterConsumption: Int) =
            WaterConsumptionDialog(context, waterConsumption)
    }
}