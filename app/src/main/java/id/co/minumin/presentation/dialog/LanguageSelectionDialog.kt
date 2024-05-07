package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isGone
import id.co.minumin.databinding.DialogLanguageSelectionBinding
import id.co.minumin.uikit.MinuminDialog
import id.co.minumin.util.LocaleHelper.LanguageCode.ENGLISH
import id.co.minumin.util.LocaleHelper.LanguageCode.INDONESIA

/**
 * Created by pertadima on 08,February,2021
 */

class LanguageSelectionDialog(
    context: Context,
    private var selectedLanguage: String
) : MinuminDialog<DialogLanguageSelectionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogLanguageSelectionBinding
        get() = DialogLanguageSelectionBinding::inflate

    private var action: (String) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        languageImageviewIndcheck.isGone = selectedLanguage == ENGLISH
        languageImageviewEncheck.isGone = selectedLanguage == INDONESIA
        dialogRelativeLanguageEnglish.setOnClickListener {
            action.invoke(ENGLISH)
            dismiss()
        }

        dialogRelativeLanguageIndonesia.setOnClickListener {
            action.invoke(INDONESIA)
            dismiss()
        }
    }

    fun setListener(action: (String) -> Unit): LanguageSelectionDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context, selectedLanguage: String) =
            LanguageSelectionDialog(context, selectedLanguage)
    }
}