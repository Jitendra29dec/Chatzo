package com.example.chatzo.databases

import android.content.ContentValues
import android.content.Context
import java.util.*

class MessagesDateDb(context: Context?) {
    var dbHelper: DbHelper?
    var context: Context?
    fun createMessageDate(date: String?, userId: String?) {
        try {
            val cv = ContentValues()
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGE_date, date)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGE_date_USERID, userId)
            dbHelper!!.writableDatabase.insert(DbHelper.Companion.TABLE_USER_MESSAGE_DATE, null, cv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUserLists(id: String): ArrayList<String> {
        val products = ArrayList<String>()
        val query = ("select * from "
                + DbHelper.Companion.TABLE_USER_MESSAGE_DATE + " WHERE " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGE_date_USERID + " =?")
        val cursor = dbHelper!!.readableDatabase.rawQuery(query, arrayOf(id))
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            do {
                products.add(cursor.getString(1))
            } while (cursor.moveToNext())
        }
        return products
    }

    fun hasObject(id: String, userId: String): Boolean {
        val selectString = "SELECT * FROM " + DbHelper.Companion.TABLE_USER_MESSAGE_DATE + " WHERE " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGE_date + " =?" + " AND " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGE_date_USERID + " =?"

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        val cursor = dbHelper!!.readableDatabase.rawQuery(selectString, arrayOf(id, userId))
        var hasObject = false
        if (cursor.moveToFirst()) {
            hasObject = true

            //region if you had multiple records to check for, use this region.
            var count = 0
            while (cursor.moveToNext()) {
                count++
            }
            //here, count is records found
            //Log.d(TAG, String.format("%d records found", count));

            //endregion
        }
        cursor.close() // Dont forget to close your cursor
        dbHelper!!.close() //AND your Database!
        return hasObject
    }

    init {
        dbHelper = DbHelper.Companion.getInstance(context)
        this.context = context
    }
}