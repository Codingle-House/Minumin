package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogPhysicalActivitiesBinding
import id.co.minumin.databinding.DialogUserSuggestionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 16,February,2021
 */

class UserSuggestionDialog(context: Context) : MinuminDialog<DialogUserSuggestionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogUserSuggestionBinding
        get() = DialogUserSuggestionBinding::inflate

    private var action: (String) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        binding.registerButtonSubmit.setOnClickListener {
            with(binding.registerFormSuggestion) {
                if (getFormText().isEmpty()) {
                    showError(true)
                    return@setOnClickListener
                }
            }

            action.invoke(binding.registerFormSuggestion.getFormText())
            dismiss()
        }
    }

    fun setListener(action: (String) -> Unit): UserSuggestionDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context) = UserSuggestionDialog(context)
    }
}