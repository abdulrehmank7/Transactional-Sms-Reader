package com.arkapp.payoassignment.data.models

import java.util.*

/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */
data class TransactionSms(
    var id: Int? = null,
    var date: Date? = null,
    var type: String? = null,
    var amount: Double? = null
)