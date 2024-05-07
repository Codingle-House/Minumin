package id.co.minumin.presentation.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import id.co.minumin.R
import id.co.minumin.databinding.ViewItemChartMarkerBinding

/**
 * Created by pertadima on 09,February,2021
 */

@SuppressLint("ViewConstructor")
class CustomChartMarkerView(
    context: Context,
    layoutResources: Int = R.layout.view_item_chart_marker,
    private val unit: String = ""
) : MarkerView(context, layoutResources) {

    private val binding: ViewItemChartMarkerBinding =
        ViewItemChartMarkerBinding.inflate(LayoutInflater.from(context), this, true)

    @SuppressLint("StringFormatMatches")
    override fun refreshContent(entry: Entry?, highlight: Highlight?) {
        binding.historyTextviewChartMarker.text = String.format(
            context.getString(R.string.general_text_chart_formatmarkerview),
            entry?.y?.toInt() ?: 0,
            unit
        )
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF? {
        return MPPointF((-(width / HALF_SIZE)).toFloat(), (-height).toFloat())
    }

    companion object {
        private const val HALF_SIZE = 2
    }
}