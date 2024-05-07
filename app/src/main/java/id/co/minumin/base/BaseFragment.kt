package id.co.minumin.base

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.annotation.ColorRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import id.co.minumin.core.ext.getColorCompat

/**
 * Created by pertadima on 31,January,2021
 */

abstract class BaseFragment<VB : ViewBinding> : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onViewReady()
        observeViewModel()
    }

    abstract fun onViewReady()

    abstract fun observeViewModel()

    protected fun <T> LiveData<T>.onResult(action: (T) -> Unit) {
        observe(this@BaseFragment, Observer { data -> data?.let(action) })
    }

    protected fun changeStatusBarColor(@ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity?.getColorCompat(color)?.let { color ->
                activity?.window?.statusBarColor = color
            }
        }
    }

    protected fun changeStatusBarTextColor(isLightStatusBar: Boolean = false) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            when {
                isLightStatusBar -> {
                    activity?.window?.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                    )
                }

                else -> {
                    activity?.window?.insetsController?.setSystemBarsAppearance(
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS.inv(),
                        WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS.inv()
                    )
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                isLightStatusBar -> {
                    activity?.window?.decorView?.systemUiVisibility =
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                }

                else -> {
                    val decorView = activity?.window?.decorView
                    decorView?.systemUiVisibility?.and(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv())
                        ?.let {
                            decorView.systemUiVisibility = it
                        }
                }
            }
        }
    }
}