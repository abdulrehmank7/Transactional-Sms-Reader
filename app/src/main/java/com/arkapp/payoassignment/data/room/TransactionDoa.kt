package com.arkapp.payoassignment.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arkapp.payoassignment.data.models.Transaction

/**
 * Created by Abdul Rehman.
 * Contact email - abdulrehman0796@gmail.com
 */

@Dao
interface TransactionDoa {

    @Query("SELECT * From TABLE_TRANSACTION")
    suspend fun getAllTransactions(): List<Transaction>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg transaction: Transaction)

    @Query("Update TABLE_TRANSACTION Set tag =:tag where id =:id")
    suspend fun setTag(tag: String, id: Int)
}