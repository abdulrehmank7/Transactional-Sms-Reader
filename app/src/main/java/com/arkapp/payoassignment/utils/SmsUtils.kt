package com.arkapp.payoassignment.utils

import android.app.Activity
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.arkapp.payoassignment.data.models.Sms
import com.arkapp.payoassignment.data.models.Transaction
import com.arkapp.payoassignment.data.models.TransactionSms
import com.arkapp.payoassignment.data.room.AppDatabase
import com.arkapp.payoassignment.data.room.AppDatabase.Companion.getDatabase
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */
val SMS_LIST = ArrayList<Sms>()
val TRANSACTIONAL_SMS_LIST = ArrayList<TransactionSms>()
var TRANSACTIONAL_LIST = ArrayList<Transaction>()

private val gson = Gson()

const val EXPENSES = "EXPENSES"
const val INCOME = "INCOME"

const val DEBIT = "debit"
const val CREDIT = "credit"

const val DEBITED = "debited"
const val TRANSFERRED = "transferred"
const val WITHDRAWN = "withdrawn"
const val CREDITED = "credited"


fun FragmentActivity.getAllSms(lifecycleCoroutineScope: LifecycleCoroutineScope) {

    val message: Uri = Uri.parse("content://sms/")

    val cursorLoader: LoaderManager.LoaderCallbacks<Cursor> = object : LoaderManager.LoaderCallbacks<Cursor> {
        override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
            return CursorLoader(this@getAllSms, message,
                null, null, null, null)
        }

        override fun onLoadFinished(loader: Loader<Cursor>, c: Cursor?) {
            val totalSMS: Int = c!!.count
            if (c.moveToFirst()) {
                for (i in 0 until totalSMS) {

                    val smsObj = createSmsObj(c)

                    if (checkIfTransactionalSms(smsObj) && smsObj.folderName == "inbox") {
                        if (smsObj.msg!!.contains(DEBITED, ignoreCase = true) ||
                            smsObj.msg!!.contains(TRANSFERRED, ignoreCase = true) ||
                            smsObj.msg!!.contains(WITHDRAWN, ignoreCase = true)) {

                            addTransactionSms(smsObj, DEBIT)

                        } else if (smsObj.msg!!.contains(CREDITED, ignoreCase = true)) {
                            addTransactionSms(smsObj, CREDIT)
                        }
                        SMS_LIST.add(smsObj)
                    }
                    c.moveToNext()
                }
            }
            addTransactionToDb(lifecycleCoroutineScope)
            TRANSACTIONAL_SMS_LIST.forEach {
                println("id ${it.id} time ${it.date} type ${it.type} amount ${it.amount}")
                println("-----------------")
            }
        }

        override fun onLoaderReset(loader: Loader<Cursor>) {}

    }
    supportLoaderManager.initLoader<Cursor>(101, null, cursorLoader)
}

fun createSmsObj(cursor: Cursor): Sms {
    val smsObj = Sms()
    smsObj.id = cursor.getString(cursor.getColumnIndexOrThrow("_id"))
    smsObj.sender = cursor.getString(cursor.getColumnIndexOrThrow("address"))
    smsObj.msg = cursor.getString(cursor.getColumnIndexOrThrow("body"))
    smsObj.readState = cursor.getString(cursor.getColumnIndex("read"))
    smsObj.time = cursor.getString(cursor.getColumnIndexOrThrow("date"))

    if (cursor.getString(cursor.getColumnIndexOrThrow("type"))!!.contains("1")) smsObj.folderName = ("inbox")
    else smsObj.folderName = ("sent")

    return smsObj
}

fun addTransactionSms(smsObj: Sms, type: String) {
    val amount = findAmount(smsObj.msg!!)

    if (amount > 0) {
        val transactionSms = TransactionSms()

        if (type == DEBIT) transactionSms.type = DEBIT
        else transactionSms.type = CREDIT

        transactionSms.amount = amount
        transactionSms.date = smsObj.time!!.toLong().getDate()
        TRANSACTIONAL_SMS_LIST.add(transactionSms)
    }
}

fun checkIfTransactionalSms(sms: Sms): Boolean {
    val reg1: Pattern = Pattern.compile("[rR][sS]\\.?\\s[,\\d]+\\.?\\d{0,2}|[iI][nN][rR]\\.?\\s*[,\\d]+\\.?\\d{0,2}\n")
    val reg2: Pattern = Pattern.compile("(?=.*[Aa]ccount.*|.*[Aa]/[Cc].*|.*[Aa][Cc][Cc][Tt].*|.*[Cc][Aa][Rr][Dd].*)(?=.*[Cc]redit.*|.*[Dd]ebit.*)(?=.*[Ii][Nn][Rr].*|.*[Rr][Ss].*)")

    if (reg1.matcher(sms.msg).find()) return true

    if (reg2.matcher(sms.msg).find()) return true

    if (sms.msg!!.contains(DEBITED, ignoreCase = true) ||
        sms.msg!!.contains(TRANSFERRED, ignoreCase = true) ||
        sms.msg!!.contains(WITHDRAWN, ignoreCase = true) ||
        sms.msg!!.contains(CREDITED, ignoreCase = true))
        return true
    return false
}

fun findAmount(smsBody: String): Double {
    val reg1: Pattern = Pattern.compile("(?i)(?:(?:RS|INR|MRP)\\.?\\s?)(\\d+(:?\\,\\d+)?(\\,\\d+)?(\\.\\d{1,2})?)")
    val decimalReg: Pattern = Pattern.compile("[0-9]+.[0-9]*|[0-9]*.[0-9]+|[0-9]+")
    val matcher: Matcher = reg1.matcher(smsBody)

    if (matcher.find()) {
        return try {
            var amountString = matcher.group(0)
            amountString = amountString.replace("Rs.", "").trim()

            val finalStringAmount = StringBuffer()
            val m: Matcher = decimalReg.matcher(amountString)

            while (m.find()) finalStringAmount.append(m.group())

            finalStringAmount.toString().toDouble()
        } catch (e: Exception) {
            0.0
        }
    }
    return 0.0
}

fun Activity.addTransactionToDb(lifecycleCoroutineScope: LifecycleCoroutineScope) {
    val database: AppDatabase = getDatabase(this)

    lifecycleCoroutineScope.launch {

        val transaction = database.transactionDao().getAllTransactions()

        if (transaction.isEmpty()) {
            for (data in TRANSACTIONAL_SMS_LIST) {
                database.transactionDao().insert(
                    Transaction(
                        null,
                        gson.toJson(data.date),
                        data.type,
                        data.amount,
                        ""
                    ))
            }
        } else {
            for (data in TRANSACTIONAL_SMS_LIST) {
                transaction.find { it.amount == data.amount && it.date == gson.toJson(data.date) && it.type == data.type }
                    .also {
                        if (it == null) {
                            database.transactionDao().insert(
                                Transaction(
                                    null,
                                    gson.toJson(data.date),
                                    data.type,
                                    data.amount,
                                    ""
                                ))
                        }
                    }
            }
        }
    }
}