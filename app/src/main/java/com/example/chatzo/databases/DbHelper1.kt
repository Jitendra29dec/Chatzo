package com.example.chatzo.databases

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DbHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_CHAT_USERS)
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_USER_MESSAGES_DATE)
        db.execSQL(CREATE_TABLE_USER_MESSAGES)
        println("database created....")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Log.w(DbHelper.class.getName(), "Upgrading database from version "
        // + oldVersion + " to " + newVersion
        // + ", which will destroy all old data");
        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART_PRODUCT);
        onCreate(db)
    }

    companion object {
        var dbHelper: DbHelper? = null
        private const val DATABASE_NAME = "tbd.chatzo.database"
        private const val DATABASE_VERSION = 6

        /*Chat Users*/
        const val TABLE_CHAT_USERS = "chat_user"
        const val TABLE_CHAT_USERS_COLUMN_ID = "id"
        const val TABLE_CHAT_USERS_COLUMN_user_id = "user_id"
        const val TABLE_CHAT_USERS_COLUMN_name = "name"
        const val TABLE_CHAT_USERS_COLUMN_profile_pic = "profile_pic"
        const val TABLE_CHAT_USERS_COLUMN_message_id = "message_id"
        const val TABLE_CHAT_USERS_COLUMN_sender_id = "sender_id"
        const val TABLE_CHAT_USERS_COLUMN_receiver_id = "receiver_id"
        const val TABLE_CHAT_USERS_COLUMN_message = "message"
        const val TABLE_CHAT_USERS_COLUMN_sent_date = "sent_date"
        const val TABLE_CHAT_USERS_COLUMN_sent_time = "sent_time"
        const val TABLE_CHAT_USERS_COLUMN_media_type = "media_type"
        const val TABLE_CHAT_USERS_COLUMN_file_name = "file_name"
        const val TABLE_CHAT_USERS_COLUMN_file_type = "file_type"
        const val TABLE_CHAT_USERS_COLUMN_file_data = "file_data"
        const val TABLE_CHAT_USERS_COLUMN_message_status = "message_status"
        const val TABLE_CHAT_USERS_COLUMN_is_delete = "is_delete"
        const val TABLE_CHAT_USERS_COLUMN_side = "side"
        const val TABLE_CHAT_USERS_COLUMN_userUnreadCount = "userUnreadCount"
        private const val CREATE_TABLE_CHAT_USERS = ("create table IF NOT EXISTS "
                + TABLE_CHAT_USERS + "(" + TABLE_CHAT_USERS_COLUMN_ID
                + " integer primary key autoincrement, "
                + TABLE_CHAT_USERS_COLUMN_user_id
                + " text ," +
                TABLE_CHAT_USERS_COLUMN_name
                + " text ,"
                + TABLE_CHAT_USERS_COLUMN_profile_pic + " text,"
                + TABLE_CHAT_USERS_COLUMN_message_id + " text,"
                + TABLE_CHAT_USERS_COLUMN_sender_id + " text,"
                + TABLE_CHAT_USERS_COLUMN_receiver_id + " text,"
                + TABLE_CHAT_USERS_COLUMN_message + " text,"
                + TABLE_CHAT_USERS_COLUMN_sent_date + " text,"
                + TABLE_CHAT_USERS_COLUMN_sent_time + " text,"
                + TABLE_CHAT_USERS_COLUMN_media_type + " text,"
                + TABLE_CHAT_USERS_COLUMN_file_name + " text,"
                + TABLE_CHAT_USERS_COLUMN_file_type + " text,"
                + TABLE_CHAT_USERS_COLUMN_file_data + " text,"
                + TABLE_CHAT_USERS_COLUMN_message_status + " text,"
                + TABLE_CHAT_USERS_COLUMN_is_delete + " text,"
                + TABLE_CHAT_USERS_COLUMN_side + " text,"
                + TABLE_CHAT_USERS_COLUMN_userUnreadCount + " text"
                + ");")

        /*Users*/
        const val TABLE_USERS = "user"
        const val TABLE_USERS_COLUMN_ID = "id"
        const val TABLE_USERS_COLUMN_user_id = "user_id"
        const val TABLE_USERS_COLUMN_name = "name"
        const val TABLE_USERS_COLUMN_profile_pic = "profile_pic"
        private const val CREATE_TABLE_USERS = ("create table IF NOT EXISTS "
                + TABLE_USERS + "(" + TABLE_USERS_COLUMN_ID
                + " integer primary key autoincrement, "
                + TABLE_USERS_COLUMN_user_id
                + " text ," +
                TABLE_USERS_COLUMN_name
                + " text ,"
                + TABLE_USERS_COLUMN_profile_pic + " text"
                + ");")

        /*Messages Date*/
        const val TABLE_USER_MESSAGE_DATE = "message_date"
        const val TABLE_USER_COLUMN_MESSAGE_ID = "id"
        const val TABLE_USER_COLUMN_MESSAGE_date = "date"
        const val TABLE_USER_COLUMN_MESSAGE_date_USERID = "userid"
        private const val CREATE_TABLE_USER_MESSAGES_DATE = ("create table IF NOT EXISTS "
                + TABLE_USER_MESSAGE_DATE + "(" + TABLE_USER_COLUMN_MESSAGE_ID
                + " integer primary key autoincrement, "
                + TABLE_USER_COLUMN_MESSAGE_date + " text,"
                + TABLE_USER_COLUMN_MESSAGE_date_USERID + " text"
                + ");")

        /*Messages*/
        const val TABLE_USER_MESSAGES = "message"
        const val TABLE_USER_COLUMN_MESSAGES_USERID = "userid"
        const val TABLE_USER_COLUMN_MESSAGES_ID = "id"
        const val TABLE_USER_COLUMN_MESSAGES_DATE_ID = "date_id"
        const val TABLE_USER_COLUMN_MESSAGES_message_id = "message_id"
        const val TABLE_USER_COLUMN_MESSAGES_sender_id = "sender_id"
        const val TABLE_USER_COLUMN_MESSAGES_receiver_id = "receiver_id"
        const val TABLE_USER_COLUMN_MESSAGES_message = "message"
        const val TABLE_USER_COLUMN_MESSAGES_sent_date = "sent_date"
        const val TABLE_USER_COLUMN_MESSAGES_sent_time = "sent_time"
        const val TABLE_USER_COLUMN_MESSAGES_delivered_time = "delivered_time"
        const val TABLE_USER_COLUMN_MESSAGES_delivered_date = "delivered_date"
        const val TABLE_USER_COLUMN_MESSAGES_seen_time = "seen_time"
        const val TABLE_USER_COLUMN_MESSAGES_seen_date = "seen_date"
        const val TABLE_USER_COLUMN_MESSAGES_media_type = "media_type"
        const val TABLE_USER_COLUMN_MESSAGES_file_name = "file_name"
        const val TABLE_USER_COLUMN_MESSAGES_file_type = "file_type"
        const val TABLE_USER_COLUMN_MESSAGES_file_data = "file_data"
        const val TABLE_USER_COLUMN_MESSAGES_message_status = "message_status"
        const val TABLE_USER_COLUMN_MESSAGES_is_delete = "is_delete"
        const val TABLE_USER_COLUMN_MESSAGES_side = "side"
        const val TABLE_USER_COLUMN_MESSAGES_unseenCount = "unseenCount"
        private const val CREATE_TABLE_USER_MESSAGES = ("create table IF NOT EXISTS "
                + TABLE_USER_MESSAGES + "(" + TABLE_USER_COLUMN_MESSAGES_ID
                + " integer primary key autoincrement, "
                + TABLE_USER_COLUMN_MESSAGES_DATE_ID + " text,"
                + TABLE_USER_COLUMN_MESSAGES_message_id + " text,"
                + TABLE_USER_COLUMN_MESSAGES_sender_id + " text,"
                + TABLE_USER_COLUMN_MESSAGES_receiver_id + " text,"
                + TABLE_USER_COLUMN_MESSAGES_message + " text,"
                + TABLE_USER_COLUMN_MESSAGES_sent_date + " text,"
                + TABLE_USER_COLUMN_MESSAGES_sent_time + " text,"
                + TABLE_USER_COLUMN_MESSAGES_delivered_time + " text,"
                + TABLE_USER_COLUMN_MESSAGES_delivered_date + " text,"
                + TABLE_USER_COLUMN_MESSAGES_seen_time + " text,"
                + TABLE_USER_COLUMN_MESSAGES_seen_date + " text,"
                + TABLE_USER_COLUMN_MESSAGES_media_type + " text,"
                + TABLE_USER_COLUMN_MESSAGES_file_name + " text,"
                + TABLE_USER_COLUMN_MESSAGES_file_type + " text,"
                + TABLE_USER_COLUMN_MESSAGES_file_data + " text,"
                + TABLE_USER_COLUMN_MESSAGES_message_status + " text,"
                + TABLE_USER_COLUMN_MESSAGES_is_delete + " text,"
                + TABLE_USER_COLUMN_MESSAGES_side + " text,"
                + TABLE_USER_COLUMN_MESSAGES_unseenCount + " text,"
                + TABLE_USER_COLUMN_MESSAGES_USERID + " text"
                + ");")

        fun getInstance(context: Context?): DbHelper? {
            if (dbHelper == null) {
                dbHelper = DbHelper(context!!)
            }
            return dbHelper
        }
    }

}