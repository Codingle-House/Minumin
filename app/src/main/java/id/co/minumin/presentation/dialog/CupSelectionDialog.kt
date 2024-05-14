package id.co.minumin.presentation.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.R
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

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog() = with(binding) {
        cupTv100.text = "${CUP_100.capacity} ${context.getString(R.string.general_text_ml)}"
        cupTv150.text = "${CUP_150.capacity} ${context.getString(R.string.general_text_ml)}"
        cupTv200.text = "${CUP_200.capacity} ${context.getString(R.string.general_text_ml)}"
        cupTv300.text = "${CUP_300.capacity} ${context.getString(R.string.general_text_ml)}"
        cupTv400.text = "${CUP_400.capacity} ${context.getString(R.string.general_text_ml)}"
        cupTvCustom.text = context.getString(R.string.general_text_custom)

        cupLinear100.setOnClickListener {
            action.invoke(CUP_100)
            dismiss()
        }
        cupLinear150.setOnClickListener {
            action.invoke(CUP_150)
            dismiss()
        }
        cupLinear200.setOnClickListener {
            action.invoke(CUP_200)
            dismiss()
        }
        cupLinear300.setOnClickListener {
            action.invoke(CUP_300)
            dismiss()
        }
        cupLinear400.setOnClickListener {
            action.invoke(CUP_400)
            dismiss()
        }
        cupLinear400.setOnClickListener {
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