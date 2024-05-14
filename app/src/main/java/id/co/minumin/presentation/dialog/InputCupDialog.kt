package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogInputCupBinding
import id.co.minumin.uikit.MinuminDialog

class InputCupDialog(context: Context, private var cupSize: Int) :
    MinuminDialog<DialogInputCupBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogInputCupBinding
        get() = DialogInputCupBinding::inflate

    private var action: (Int) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        binding.inputCupFormSize.setText(cupSize.toString())
        binding.inputCupButtonSubmit.setOnClickListener {
            with(binding.inputCupFormSize) {
                if (getFormText().isEmpty()) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            cupSize = binding.inputCupFormSize.getFormText().toInt()
            with(binding.inputCupFormSize) {
                if (cupSize == 0) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            if (cupSize == 0) return@setOnClickListener

            action.invoke(cupSize)
            dismiss()
        }
    }

    fun setListener(action: (Int) -> Unit): InputCupDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context, cupSize: Int) = InputCupDialog(context, cupSize)
    }
}