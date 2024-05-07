package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogBodyWeightBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 08,February,2021
 */

class BodyWeightDialog(context: Context, private var bodyWeight: Int) :
    MinuminDialog<DialogBodyWeightBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogBodyWeightBinding
        get() = DialogBodyWeightBinding::inflate

    private var action: (Int) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        binding.registerFormWeight.setText(bodyWeight.toString())
        binding.registerButtonSubmit.setOnClickListener {
            with(binding.registerFormWeight) {
                if (getFormText().isEmpty()) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            bodyWeight = binding.registerFormWeight.getFormText().toInt()
            with(binding.registerFormWeight) {
                if (bodyWeight == 0) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            if (bodyWeight == 0) return@setOnClickListener

            action.invoke(bodyWeight)
            dismiss()
        }
    }

    fun setListener(action: (Int) -> Unit): BodyWeightDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context, bodyWeight: Int) = BodyWeightDialog(context, bodyWeight)
    }
}