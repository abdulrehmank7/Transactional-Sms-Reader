package com.arkapp.payoassignment.ui.history

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.arkapp.payoassignment.data.room.AppDatabase
import com.arkapp.payoassignment.utils.TRANSACTIONAL_LIST
import kotlinx.coroutines.launch

/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */

fun Context.updateTag(lifecycleScope: LifecycleCoroutineScope, tag: String, id: Int) {
    val database: AppDatabase = AppDatabase.getDatabase(this)
    lifecycleScope.launch {
        TRANSACTIONAL_LIST.find { it.id == id }?.tag = tag
        database.transactionDao().setTag(tag, id)
    }
}