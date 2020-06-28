package com.arkapp.payoassignment.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Created by Abdul Rehman on 16-05-2020.
 * Contact email - abdulrehman0796@gmail.com
 */
@Entity(tableName = "TABLE_TRANSACTION")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,

    @ColumnInfo(name = "date")
    val date: String?,

    @ColumnInfo(name = "type")
    val type: String?,

    @ColumnInfo(name = "amount")
    val amount: Double?,

    @ColumnInfo(name = "tag")
    var tag: String?
)