package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.data.dto.PhysicalActivitiesDto.ACTIVE
import id.co.minumin.data.dto.PhysicalActivitiesDto.NORMAL
import id.co.minumin.data.dto.PhysicalActivitiesDto.VERY_ACTIVE
import id.co.minumin.databinding.DialogMetricSelectionBinding
import id.co.minumin.databinding.DialogPhysicalActivitiesBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class PhysicalActivitiesDialog(
    context: Context
) : MinuminDialog<DialogPhysicalActivitiesBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogPhysicalActivitiesBinding
        get() = DialogPhysicalActivitiesBinding::inflate

    private var action: (PhysicalActivitiesDto) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        binding.physicalRelativelayoutNormal.setOnClickListener {
            action.invoke(NORMAL)
            dismiss()
        }
        binding.physicalRelativelayoutActive.setOnClickListener {
            action.invoke(ACTIVE)
            dismiss()
        }
        binding.physicalRelativelayoutExtra.setOnClickListener {
            action.invoke(VERY_ACTIVE)
            dismiss()
        }
    }

    fun setListener(action: (PhysicalActivitiesDto) -> Unit): PhysicalActivitiesDialog {
        this.action = action
        return this
    }

    companion object {
        fun newInstance(context: Context) = PhysicalActivitiesDialog(context)
    }
}