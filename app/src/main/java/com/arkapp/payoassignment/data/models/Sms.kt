package com.arkapp.payoassignment.data.models

/**
 * Created by Abdul Rehman on 28-06-2020.
 * Contact email - abdulrehman0796@gmail.com
 */
data class Sms(
    var id: String? = null,
    var sender: String? = null,
    var msg: String? = null,
    var readState: String? = null,
    var time: String? = null,
    var folderName: String? = null
)