package com.example.chatzo.databases

import android.content.ContentValues
import android.content.Context
import com.example.chatzo.Models.MessagesModel
import java.util.*

class MessagesDb(context: Context?) {
    var dbHelper: DbHelper?
    var context: Context?
    fun createMessage(messagesModel: MessagesModel) {
        try {
            val cv = ContentValues()
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_DATE_ID, messagesModel.dateId)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_message_id, messagesModel.id)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_sender_id, messagesModel.sender_id)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_receiver_id, messagesModel.receiver_id)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_message, messagesModel.message)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_sent_date, messagesModel.sent_date)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_sent_time, messagesModel.sent_time)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_delivered_time, messagesModel.delivered_time)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_delivered_date, messagesModel.delivered_date)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_seen_time, messagesModel.seen_time)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_seen_date, messagesModel.seen_date)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_media_type, messagesModel.media_type)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_file_name, messagesModel.file_name)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_file_type, messagesModel.file_type)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_file_data, messagesModel.file_data)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_message_status, messagesModel.message_status)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_is_delete, messagesModel.is_delete)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_side, messagesModel.side)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_unseenCount, "" + messagesModel.unseenCount)
            cv.put(DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_USERID, "" + messagesModel.userId)
            dbHelper!!.writableDatabase.insert(DbHelper.Companion.TABLE_USER_MESSAGES, null, cv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getUserLists(id: String, userId: String): ArrayList<MessagesModel> {
        val products = ArrayList<MessagesModel>()
        val query = ("select * from "
                + DbHelper.Companion.TABLE_USER_MESSAGES + " WHERE " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_DATE_ID + " =?" + " AND " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_USERID + " =?")
        // Cursor cursor = dbHelper.getReadableDatabase().rawQuery(query, null);
        val cursor = dbHelper!!.readableDatabase.rawQuery(query, arrayOf(id, userId))
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            do {
                val messagesModel = MessagesModel()
                messagesModel.dateId = cursor.getString(1)
                messagesModel.id = cursor.getString(2)
                messagesModel.sender_id = cursor.getString(3)
                messagesModel.receiver_id = cursor.getString(4)
                messagesModel.message = cursor.getString(5)
                messagesModel.sent_date = cursor.getString(6)
                messagesModel.sent_time = cursor.getString(7)
                messagesModel.delivered_time = cursor.getString(8)
                messagesModel.delivered_date = cursor.getString(9)
                messagesModel.seen_time = cursor.getString(10)
                messagesModel.seen_date = cursor.getString(11)
                messagesModel.media_type = cursor.getString(12)
                messagesModel.file_name = cursor.getString(13)
                messagesModel.file_type = cursor.getString(14)
                messagesModel.file_data = cursor.getString(15)
                messagesModel.message_status = cursor.getString(16)
                messagesModel.is_delete = cursor.getString(17)
                messagesModel.side = cursor.getString(18)
                messagesModel.unseenCount = cursor.getString(19).toInt()
                messagesModel.userId = cursor.getString(20)
                products.add(messagesModel)
            } while (cursor.moveToNext())
        }
        return products
    }

    fun hasObject(id: String): Boolean {
        val selectString = "SELECT * FROM " + DbHelper.Companion.TABLE_USER_MESSAGES + " WHERE " + DbHelper.Companion.TABLE_USER_COLUMN_MESSAGES_message_id + " =?"

        // Add the String you are searching by here.
        // Put it in an array to avoid an unrecognized token error
        val cursor = dbHelper!!.readableDatabase.rawQuery(selectString, arrayOf(id))
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