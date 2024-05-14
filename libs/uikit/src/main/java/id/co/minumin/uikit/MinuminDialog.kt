package id.co.minumin.uikit

import Minumin.R
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import android.view.Window.FEATURE_NO_TITLE
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.MATCH_PARENT
import android.view.WindowManager.LayoutParams.WRAP_CONTENT
import androidx.viewbinding.ViewBinding

/**
 * Created by pertadima on 07,February,2021
 */

abstract class MinuminDialog<VB : ViewBinding>(
    context: Context
) : Dialog(context, R.style.BaseDialogSlideAnim) {
    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    abstract val bindingInflater: (LayoutInflater) -> VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setupDialog()
        setContentView(binding.root)
        onCreateDialog()
    }

    private fun setupDialog() {
        requestWindowFeature(FEATURE_NO_TITLE)
        window?.setBackgroundDrawableResource(android.R.color.transparent);
        window?.setLayout(MATCH_PARENT, WRAP_CONTENT)
    }

    abstract fun onCreateDialog()
}