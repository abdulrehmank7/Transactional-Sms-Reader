package com.arkapp.payoassignment.ui.home

import com.arkapp.payoassignment.data.models.Transaction
import com.arkapp.payoassignment.data.models.TransactionSms
import com.arkapp.payoassignment.utils.getCalender
import com.google.gson.Gson
import java.util.*

/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */

private val gson = Gson()

const val TYPE_ALL = 0
const val TYPE_MONTH = 1
const val TYPE_DAY = 2

fun filterDayTransactions(transaction: List<Transaction>, calendar: Calendar): ArrayList<Transaction> {
    val filteredTransaction = ArrayList<Transaction>()
    for (data in transaction) {
        val transactionSms = convertTransaction(data)
        if (transactionSms.date!!.getCalender().get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)) {
            filteredTransaction.add(data)
        }
    }
    return filteredTransaction
}

fun filterMonthTransactions(transaction: List<Transaction>, calendar: Calendar): ArrayList<Transaction> {
    val filteredTransaction = ArrayList<Transaction>()

    val maximumDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val maxCalendar = Calendar.getInstance().also {
        it.time = calendar.time
        it.set(Calendar.DAY_OF_MONTH, maximumDate)
        it.set(Calendar.HOUR_OF_DAY, 23)
        it.set(Calendar.MINUTE, 59)
    }

    val minCalendar = Calendar.getInstance().also {
        it.time = calendar.time
        it.set(Calendar.DAY_OF_MONTH, 0)
        it.set(Calendar.HOUR_OF_DAY, 0)
        it.set(Calendar.MINUTE, 0)
    }

    val maxDate = maxCalendar.time
    val minDate = minCalendar.time

    for (data in transaction) {
        val transactionSms = convertTransaction(data)
        if (transactionSms.date!!.after(minDate) && transactionSms.date!!.before(maxDate)) {
            filteredTransaction.add(data)
        }
    }
    return filteredTransaction
}

fun convertTransaction(transaction: Transaction): TransactionSms {
    val transactionSms = TransactionSms()
    transactionSms.amount = transaction.amount
    transactionSms.type = transaction.type
    transactionSms.id = transaction.id
    transactionSms.date = gson.fromJson(transaction.date, Date::class.java)
    return transactionSms
}

fun filterData(transactions: List<Transaction>, type: Int, selectedCal: Calendar): List<Transaction> {
    when (type) {
        TYPE_ALL   -> return transactions
        TYPE_MONTH -> return filterMonthTransactions(transactions, selectedCal)
        TYPE_DAY   -> return filterDayTransactions(transactions, selectedCal)
    }
    return transactions
}