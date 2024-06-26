package id.co.minumin.base

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.M
import android.os.Build.VERSION_CODES.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.viewbinding.ViewBinding
import id.co.minumin.core.ext.getColorCompat

/**
 * Created by pertadima on 26,January,2021
 */

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    abstract val bindingInflater: (LayoutInflater) -> VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        onViewCreated()
        onViewModelObserver()
    }

    abstract fun onViewCreated()
    abstract fun onViewModelObserver()

    protected fun <T> LiveData<T>.onResult(action: (T) -> Unit) {
        observe(this@BaseActivity) { data -> data?.let(action) }
    }

    protected fun changeStatusBarColor(@ColorRes color: Int) {
        window.statusBarColor = getColorCompat(color)
    }

    protected fun changeStatusBarTextColor(isLightStatusBar: Boolean = false) {
        if (SDK_INT >= R) {
            when {
                isLightStatusBar -> window.insetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS,
                    APPEARANCE_LIGHT_STATUS_BARS
                )

                else -> window.insetsController?.setSystemBarsAppearance(
                    APPEARANCE_LIGHT_STATUS_BARS.inv(),
                    APPEARANCE_LIGHT_STATUS_BARS.inv()
                )
            }
        } else if (SDK_INT >= M) {
            when {
                isLightStatusBar -> window.decorView.systemUiVisibility =
                    SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

                else -> {
                    val decorView = window.decorView
                    decorView.systemUiVisibility = decorView.systemUiVisibility and
                            SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
    }
}