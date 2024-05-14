package id.co.minumin.presentation.dialog.physicalactivities

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.LinearLayoutManager
import id.co.minumin.core.DiffCallback
import id.co.minumin.data.dto.PhysicalActivitiesDto
import id.co.minumin.databinding.DialogPhysicalActivitiesBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 07,February,2021
 */

class PhysicalActivitiesDialog(
    context: Context,
    diffCallback: DiffCallback
) : MinuminDialog<DialogPhysicalActivitiesBinding>(context) {

    private val physicalActivitiesAdapter by lazy {
        PhysicalActivitiesAdapter(context, diffCallback, ::onPhysicalActivitiesClicked).apply {
            setData(PhysicalActivitiesDto.entries.toTypedArray().toList())
        }
    }

    override val bindingInflater: (LayoutInflater) -> DialogPhysicalActivitiesBinding
        get() = DialogPhysicalActivitiesBinding::inflate

    private var action: (PhysicalActivitiesDto) -> Unit = { _ -> kotlin.run { } }

    override fun onCreateDialog() {
        with(binding) {
            physicalRvContent.apply {
                adapter = physicalActivitiesAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    fun setListener(action: (PhysicalActivitiesDto) -> Unit): PhysicalActivitiesDialog {
        this.action = action
        return this
    }

    private fun onPhysicalActivitiesClicked(data: PhysicalActivitiesDto) {
        action(data)
        this.dismiss()
    }

    companion object {
        fun newInstance(
            context: Context,
            diffCallback: DiffCallback
        ) = PhysicalActivitiesDialog(context, diffCallback)
    }
}