package com.arkapp.payoassignment.ui.home

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.View
import android.widget.DatePicker
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.arkapp.payoassignment.R
import com.arkapp.payoassignment.data.models.Transaction
import com.arkapp.payoassignment.data.room.AppDatabase
import com.arkapp.payoassignment.utils.*
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var selectedCal: Calendar = Calendar.getInstance()

    private val listener by lazy {
        OnDateSetListener { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            val selectedDate = Calendar.getInstance()
            selectedDate[Calendar.YEAR] = year
            selectedDate[Calendar.MONTH] = month
            selectedDate[Calendar.DAY_OF_MONTH] = dayOfMonth

            selectedCal = selectedDate
            setSelectionDate(selectedCal)
        }
    }

    private val datePicker by lazy {
        DatePickerDialog(requireContext(),
            listener,
            selectedCal.get(Calendar.YEAR),
            selectedCal.get(Calendar.MONTH),
            selectedCal.get(Calendar.DAY_OF_MONTH))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPieChart()
        fetchData()

        btGroupExpenses.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (checkedId == R.id.btAllExpenses) {
                if (isChecked) btChangeSelection.invisible()
                else btChangeSelection.show()
            } else setSelectionDate(selectedCal)

            refreshPieData()
        }

        btChangeSelection.setOnClickListener { datePicker.show() }
    }

    private fun setSelectionDate(currentCal: Calendar) {
        if (btGroupExpenses.checkedButtonId == R.id.btMonthExpenses)
            btChangeSelection.text = currentCal.time.getSelectionMonth()
        else if (btGroupExpenses.checkedButtonId == R.id.btDayExpenses)
            btChangeSelection.text = currentCal.time.getSelectionDay()

        refreshPieData()
    }

    private fun fetchData() {
        val database: AppDatabase = AppDatabase.getDatabase(requireContext())
        lifecycleScope.launch {

            TRANSACTIONAL_LIST = ArrayList(database.transactionDao().getAllTransactions())
            setPieData(TRANSACTIONAL_LIST)
        }
    }

    private fun refreshPieData() {
        when (btGroupExpenses.checkedButtonId) {
            R.id.btMonthExpenses -> setPieData(filterData(TRANSACTIONAL_LIST, TYPE_MONTH, selectedCal))
            R.id.btDayExpenses   -> setPieData(filterData(TRANSACTIONAL_LIST, TYPE_DAY, selectedCal))
            else                 -> setPieData(filterData(TRANSACTIONAL_LIST, TYPE_ALL, selectedCal))
        }
    }

    private fun initPieChart() {
        pieChart.animateXY(0, 0)
        pieChart.legend.isWordWrapEnabled = true
        pieChart.legend.textSize = 16f
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.transparent))
        pieChart.description.isEnabled = false
    }

    private fun setPieData(data: List<Transaction>) {
        pieChart.clear()

        val chartData = ArrayList<PieEntry>()
        val legendEntries = ArrayList<LegendEntry>()
        val colors = ArrayList<Int>()

        var expense = 0f
        var income = 0f

        for (transaction in data) {

            when (transaction.type) {
                CREDIT -> income += transaction.amount!!.toFloat()
                DEBIT  -> expense += transaction.amount!!.toFloat()
            }
        }

        if (expense > 0) {
            chartData.add(PieEntry(expense, EXPENSES))
            colors.add(ContextCompat.getColor(requireContext(), R.color.red))
        }
        legendEntries.add(LegendEntry(
            EXPENSES,
            Legend.LegendForm.CIRCLE,
            18f, 18f,
            null,
            ContextCompat.getColor(requireContext(), R.color.red)))

        if (income > 0) {
            chartData.add(PieEntry(income, INCOME))
            colors.add(ContextCompat.getColor(requireContext(), R.color.green))
        }
        legendEntries.add(LegendEntry(
            INCOME,
            Legend.LegendForm.CIRCLE,
            18f, 18f,
            null,
            ContextCompat.getColor(requireContext(), R.color.green)))


        val dataSet = PieDataSet(chartData, "Expense this month")
        dataSet.valueFormatter = PieValueFormatter()
        dataSet.colors = colors
        val pieData = PieData(dataSet)
        pieData.setValueTextSize(18f)
        pieChart.legend.setCustom(legendEntries)
        pieChart.data = pieData
    }

}