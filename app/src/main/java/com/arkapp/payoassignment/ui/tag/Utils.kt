package com.arkapp.payoassignment.ui.tag

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Environment
import com.arkapp.payoassignment.data.models.Transaction
import com.arkapp.payoassignment.utils.DEBIT
import com.arkapp.payoassignment.utils.TRANSACTIONAL_LIST
import com.github.mikephil.charting.charts.BarChart
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */

fun getAllTags(): ArrayList<String> {
    val tags = ArrayList<String>()
    for (transaction in TRANSACTIONAL_LIST) {
        if (!transaction.tag.isNullOrEmpty() && !tags.contains(transaction.tag!!)) {
            tags.add(transaction.tag!!)
        }
    }
    return tags
}

fun getTagExpenses(tag: String): ArrayList<Transaction> {
    val tagTransaction = ArrayList<Transaction>()

    for (data in TRANSACTIONAL_LIST) {
        if (data.tag == tag && data.type == DEBIT)
            tagTransaction.add(data)
    }

    return tagTransaction
}

fun getTotalExpenses(transactions: ArrayList<Transaction>): Double {
    var total = 0.0
    for (data in transactions) total += data.amount!!
    return total
}

fun Activity.takePostBitmap(barChart: BarChart): Bitmap? {
    val bitmap = Bitmap.createBitmap(barChart.width, barChart.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    barChart.draw(canvas)
    return bitmap
}


fun convertBitmapToFile(destinationFile: File, bitmap: Bitmap) {
    //create a file to write bitmap data
    destinationFile.createNewFile()
    //Convert bitmap to byte array
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
    val bitmapData = bos.toByteArray()
    //write the bytes in file
    val fos = FileOutputStream(destinationFile)
    fos.write(bitmapData)
    fos.flush()
    fos.close()
}

fun Context.getFileDestination() =
    File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "sharedImage.jpg")

fun Context.shareImage(imageUri: Uri, textMsg: String, shareDialogTitle: String) {

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, textMsg)
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/*"
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(shareIntent, shareDialogTitle))
}
