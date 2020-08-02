package com.example.chatzo.databases

import android.content.ContentValues
import android.content.Context
import com.example.chatzo.Models.ChatUserListModel
import java.util.*

class ChatUsersDb(context: Context?) {
    var dbHelper: DbHelper?
    var context: Context?
    fun createUsers(model: ChatUserListModel) {
        try {
            val cv = ContentValues()
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_user_id, model.user_id)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_name, model.name)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_profile_pic, model.profile_pic)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_message_id, model.id)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_sender_id, model.sender_id)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_receiver_id, model.receiver_id)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_message, model.message)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_sent_date, model.sent_date)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_sent_time, model.sent_time)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_media_type, model.media_type)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_file_name, model.file_name)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_file_type, model.file_type)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_file_data, model.file_data)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_message_status, model.message_status)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_is_delete, model.is_delete)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_side, model.side)
            cv.put(DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_userUnreadCount, model.userUnreadCount)
            dbHelper!!.writableDatabase.insert(DbHelper.Companion.TABLE_CHAT_USERS, null, cv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    val userLists: ArrayList<ChatUserListModel>
        get() {
            val products = ArrayList<ChatUserListModel>()
            val query = ("select * from "
                    + DbHelper.Companion.TABLE_CHAT_USERS)
            val cursor = dbHelper!!.readableDatabase.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    val list = ChatUserListModel()
                    list.user_id = cursor.getString(1)
                    list.name = cursor.getString(2)
                    list.profile_pic = cursor.getString(3)
                    list.id = cursor.getString(4)
                    list.sender_id = cursor.getString(5)
                    list.receiver_id = cursor.getString(6)
                    list.message = cursor.getString(7)
                    list.sent_date = cursor.getString(8)
                    list.sent_time = cursor.getString(9)
                    list.media_type = cursor.getString(10)
                    list.file_name = cursor.getString(11)
                    list.file_type = cursor.getString(12)
                    list.file_data = cursor.getString(13)
                    list.message_status = cursor.getString(14)
                    list.is_delete = cursor.getString(15)
                    list.side = cursor.getString(16)
                    list.userUnreadCount = cursor.getString(17)
                    products.add(list)
                } while (cursor.moveToNext())
            }
            return products
        }

    fun hasObject(id: String): Boolean {
        val selectString = "SELECT * FROM " + DbHelper.Companion.TABLE_CHAT_USERS + " WHERE " + DbHelper.Companion.TABLE_CHAT_USERS_COLUMN_user_id + " =?"

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