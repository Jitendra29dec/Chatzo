package com.example.chatzo.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns._ID

class Call_logs_DB(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "call_logs"
        val SQL_CREATE_TABLE = ("CREATE TABLE " + Call_entry.TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY," + Call_entry.COLUMN_NAME_1 + " TEXT," + Call_entry.COLUMN_NAME_2 + " TEXT," + Call_entry.COLUMN_NAME_3 + " TEXT)")
    }
}