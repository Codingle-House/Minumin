package id.co.minumin.presentation.dialog.cupselection

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.GridLayoutManager
import id.co.minumin.core.DiffCallback
import id.co.minumin.data.dto.CupSelectionDto
import id.co.minumin.databinding.DialogCupSelectionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class CupSelectionDialog(
    context: Context,
    diffCallback: DiffCallback
) : MinuminDialog<DialogCupSelectionBinding>(context) {

    private val cupSelectionAdapter by lazy {
        CupSelectionAdapter(context, diffCallback, ::onCupSelectionClicked).apply {
            setData(CupSelectionDto.entries.toTypedArray().toList())
        }
    }

    override val bindingInflater: (LayoutInflater) -> DialogCupSelectionBinding
        get() = DialogCupSelectionBinding::inflate

    private var action: (CupSelectionDto) -> Unit = { _ -> kotlin.run { } }

    @SuppressLint("SetTextI18n")
    override fun onCreateDialog() {
        with(binding) {
            cupselectionRvContent.apply {
                adapter = cupSelectionAdapter
                layoutManager = GridLayoutManager(context, SPAN_COUNT)
            }
        }
    }

    fun setListener(action: (CupSelectionDto) -> Unit): CupSelectionDialog {
        this.action = action
        return this
    }

    private fun onCupSelectionClicked(data: CupSelectionDto) {
        action(data)
        this.dismiss()
    }

    companion object {
        private const val SPAN_COUNT = 3

        fun newInstance(context: Context, diffCallback: DiffCallback) =
            CupSelectionDialog(context, diffCallback)
    }
}