package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogMetricSelectionBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 08,February,2021
 */

class MetricSelectionDialog(context: Context) :
    MinuminDialog<DialogMetricSelectionBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogMetricSelectionBinding
        get() = DialogMetricSelectionBinding::inflate

    override fun onCreateDialog() {

    }

    companion object {
        fun newInstance(context: Context) = MetricSelectionDialog(context)
    }
}