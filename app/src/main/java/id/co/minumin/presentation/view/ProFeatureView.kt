package id.co.minumin.presentation.view

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import id.co.minumin.R
import id.co.minumin.databinding.ViewFeatureProBinding

/**
 * Created by pertadima on 06,February,2021
 */

class ProFeatureView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var binding: ViewFeatureProBinding? = null
    private val viewHandler by lazy {
        Handler()
    }

    private var animationRunnable: Runnable? = null

    private var action: (Action) -> Unit = { _ -> kotlin.run { } }
    private var showAsDialog: Boolean = true

    init {
        binding = ViewFeatureProBinding.inflate(LayoutInflater.from(context), this, true)
        inflate(context, R.layout.view_feature_pro, this)
        isGone = true
        hideWithAnimation()
        binding?.proCardviewContainer?.setOnClickListener {
            action.invoke(Action.Click)
        }
    }

    fun showWithAnimation(showAsDialog: Boolean = true) {
        if (isVisible.not()) {
            this.showAsDialog = showAsDialog
            animationRunnable = Runnable {
                val slideUp = AnimationUtils.loadAnimation(context, R.anim.anim_slide_up)
                isGone = false
                startAnimation(slideUp)
            }
            animationRunnable?.let {
                viewHandler.postDelayed(it, SHOW_DELAY)
            }
        }
    }

    private fun hideWithAnimation() {
        binding?.proImageviewClose?.setOnClickListener {
            if (showAsDialog) {
                val slideUp = AnimationUtils.loadAnimation(context, R.anim.anim_slide_bottom)
                startAnimation(slideUp)
            }
            isGone = true
            action.invoke(Action.Close)
        }
    }

    fun setListener(action: (Action) -> Unit): ProFeatureView {
        this.action = action
        return this
    }

    sealed class Action {
        data object Click : Action()
        data object Close : Action()
    }

    companion object {
        private const val SHOW_DELAY = 800L
    }
}