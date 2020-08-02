package com.example.chatzo.databases

import android.provider.BaseColumns

object Call_entry : BaseColumns {
    const val TABLE_NAME = "call_log_db"
    const val COLUMN_NAME_1 = "caller_name"
    const val COLUMN_NAME_2 = "call_time"
    const val COLUMN_NAME_3 = "call_status"
}