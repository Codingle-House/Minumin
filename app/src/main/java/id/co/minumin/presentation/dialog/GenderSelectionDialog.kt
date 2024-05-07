package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import id.co.minumin.const.MinuminConstant.MAN
import id.co.minumin.const.MinuminConstant.WOMAN
import id.co.minumin.core.ext.shakeAnimation
import id.co.minumin.databinding.DialogGenderSelectionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 08,February,2021
 */

class GenderSelectionDialog(
    context: Context, private var selectedGender: String
) : MinuminDialog<DialogGenderSelectionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogGenderSelectionBinding
        get() = DialogGenderSelectionBinding::inflate

    private var action: (String) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        registerImageviewCircleMan.isGone = selectedGender == WOMAN || selectedGender.isEmpty()
        registerImageviewMan.setOnClickListener { onGenderSelected(MAN) }
        registerImageviewCircleWoman.isGone = selectedGender == MAN || selectedGender.isEmpty()
        registerImageviewWoman.setOnClickListener { onGenderSelected(WOMAN) }
        registerButtonSubmit.setOnClickListener {
            with(binding.registerTextviewError) {
                if (selectedGender.isEmpty()) shakeAnimation() else clearAnimation()
                isGone = selectedGender.isNotEmpty()
            }

            if (selectedGender.isEmpty()) return@setOnClickListener
            action.invoke(selectedGender)
            dismiss()
        }
    }

    private fun onGenderSelected(gender: String) {
        selectedGender = gender
        binding.registerImageviewCircleWoman.isInvisible = gender == MAN
        binding.registerImageviewCircleMan.isInvisible = gender == WOMAN
    }

    fun setListener(action: (String) -> Unit): GenderSelectionDialog {
        this.action = action
        return this
    }

    companion object {
        fun newInstance(context: Context, selectedGender: String): GenderSelectionDialog {
            return GenderSelectionDialog(context, selectedGender)
        }
    }
}