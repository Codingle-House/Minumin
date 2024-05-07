package id.co.minumin.util

import android.view.View
import androidx.core.widget.NestedScrollView

import me.everything.android.ui.overscroll.adapters.IOverScrollDecoratorAdapter


/**
 * Created by pertadima on 07,February,2021
` */

class NestedScrollViewOverScrollDecorAdapter(view: NestedScrollView) :
    IOverScrollDecoratorAdapter {
    protected val mView: NestedScrollView = view
    override fun getView(): View {
        return mView
    }

    override fun isInAbsoluteStart(): Boolean {
        return !mView.canScrollVertically(-1)
    }

    override fun isInAbsoluteEnd(): Boolean {
        return !mView.canScrollVertically(1)
    }

}