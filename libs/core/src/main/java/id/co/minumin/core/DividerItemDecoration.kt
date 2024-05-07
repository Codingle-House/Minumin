package id.co.minumin.core

import android.R
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


/**
 * Created by pertadima on 11,February,2021
 */

class DividerItemDecoration : ItemDecoration {
    private var divider: Drawable?

    /**
     * Default divider will be used
     */
    constructor(context: Context) {
        val styledAttributes: TypedArray =
            context.obtainStyledAttributes(ATTRS)
        divider = styledAttributes.getDrawable(0)
        styledAttributes.recycle()
    }

    /**
     * Custom divider will be used
     */
    constructor(context: Context, resId: Int) {
        divider = ContextCompat.getDrawable(context, resId)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params =
                child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + (divider?.intrinsicHeight ?: 0)
            divider?.setBounds(left, top, right, bottom)
            divider?.draw(c)
        }
    }


    companion object {
        private val ATTRS = intArrayOf(R.attr.listDivider)
    }
}