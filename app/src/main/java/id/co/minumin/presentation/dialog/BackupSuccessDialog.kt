package id.co.minumin.presentation.dialog

import android.content.Context
import android.view.LayoutInflater
import id.co.minumin.databinding.DialogBackupSuccessBinding
import id.co.minumin.uikit.MinuminDialog

/**
 * Created by pertadima on 17,February,2021
 */

class BackupSuccessDialog(context: Context) : MinuminDialog<DialogBackupSuccessBinding>(context) {

    override val bindingInflater: (LayoutInflater) -> DialogBackupSuccessBinding
        get() = DialogBackupSuccessBinding::inflate

    private var action: () -> Unit = { kotlin.run { } }

    override fun onCreateDialog() = with(binding) {
        backupdialogButtonDismiss.setOnClickListener { dismiss() }
        backupdialogButtonExplore.setOnClickListener {
            action.invoke()
            dismiss()
        }
    }

    fun setListener(action: () -> Unit): BackupSuccessDialog {
        this.action = action
        return this
    }


    companion object {
        fun newInstance(context: Context): BackupSuccessDialog {
            return BackupSuccessDialog(context)
        }
    }
}