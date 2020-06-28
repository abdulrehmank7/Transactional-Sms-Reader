package com.arkapp.payoassignment.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.arkapp.payoassignment.data.models.Transaction

/**
 * Created by Abdul Rehman.
 * Contact email - abdulrehman0796@gmail.com
 */


@Database(entities = [Transaction::class], version = 1)
abstract class AppDatabase : RoomDatabase() {


    abstract fun transactionDao(): TransactionDoa

    companion object {

        fun getDatabase(context: Context): AppDatabase {
            val DATABASE_NAME = "MAIN_DATABASE"

            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, DATABASE_NAME
            ).build()
        }
    }
}