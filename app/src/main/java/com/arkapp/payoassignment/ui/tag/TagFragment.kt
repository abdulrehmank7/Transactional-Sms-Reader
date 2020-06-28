package com.arkapp.payoassignment.ui.tag

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.arkapp.payoassignment.R
import com.arkapp.payoassignment.utils.PieValueFormatter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.android.synthetic.main.fragment_tag.*
import java.util.*


class TagFragment : Fragment(R.layout.fragment_tag) {

    private lateinit var allTags: ArrayList<String>
    private lateinit var postImageUri: Uri

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initChartSetting()
        initChartData()

        btShareGraph.setOnClickListener {
            requireContext().shareImage(
                postImageUri,
                "Expenses graph by tags",
                "Select the app to send the graph.")
        }

    }

    private fun initChartSetting() {
        barChart.setFitBars(true)
        barChart.animateXY(500, 500)
        barChart.legend.isWordWrapEnabled = true
        barChart.legend.textSize = 16f
        barChart.description.isEnabled = false
        barChart.xAxis.isEnabled = true

    }

    private fun initChartData() {
        barChart.clear()
        initChartSetting()
        val entries: MutableList<BarEntry> = ArrayList()

        allTags = getAllTags()
        allTags.forEachIndexed { index, tag ->
            val transactions = getTagExpenses(tag)
            val totalExpense = getTotalExpenses(transactions)
            entries.add(BarEntry((index + 1).toFloat(), totalExpense.toFloat()))
        }
        val set = BarDataSet(entries, "Tags Expense")
        set.valueFormatter = PieValueFormatter()
        val data = BarData(set)
        data.barWidth = .5f
        barChart.setFitBars(true)
        barChart.data = data

        val xAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.axisMinimum = 0f
        xAxis.mAxisMaximum = (allTags.size).toFloat()
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt() - 1
                return if (index > allTags.size - 1 || index < 0)
                    ""
                else
                    allTags[index].toUpperCase()
            }
        }
        Handler().postDelayed({fetchImage()},1000)
    }

    private fun fetchImage() {
        try {
            val shareImg = requireActivity().takePostBitmap(barChart)

            val postImgFileInStorage = requireContext().getFileDestination()

            convertBitmapToFile(postImgFileInStorage, shareImg!!)

            postImageUri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().applicationContext.packageName + ".customProvider",
                postImgFileInStorage)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}