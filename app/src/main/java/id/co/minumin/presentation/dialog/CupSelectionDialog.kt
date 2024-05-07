package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.data.dto.CupSelectionDto.CUP_100
import id.co.minumin.data.dto.CupSelectionDto.CUP_150
import id.co.minumin.data.dto.CupSelectionDto.CUP_200
import id.co.minumin.data.dto.CupSelectionDto.CUP_300
import id.co.minumin.data.dto.CupSelectionDto.CUP_400
import id.co.minumin.databinding.DialogCupSelectionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class CupSelectionDialog(context: Context) : MinuminDialog<DialogCupSelectionBinding>(context) {
    override val bindingInflater: (LayoutInflater) -> DialogCupSelectionBinding
        get() = DialogCupSelectionBinding::inflate

    private var action: (CupSelectionDto) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        cupImageview100.setOnClickListener {
            action.invoke(CUP_100)
            dismiss()
        }
        cupImageview150.setOnClickListener {
            action.invoke(CUP_150)
            dismiss()
        }
        cupImageview200.setOnClickListener {
            action.invoke(CUP_200)
            dismiss()
        }
        cupImageview300.setOnClickListener {
            action.invoke(CUP_300)
            dismiss()
        }
        cupImageview400.setOnClickListener {
            action.invoke(CUP_400)
            dismiss()
        }
    }

    fun setListener(action: (CupSelectionDto) -> Unit): CupSelectionDialog {
        this.action = action
        return this
    }

    companion object {
        fun newInstance(context: Context) = CupSelectionDialog(context)
    }
}