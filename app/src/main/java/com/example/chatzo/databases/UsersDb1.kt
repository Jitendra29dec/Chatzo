package com.example.chatzo.databases

import android.content.ContentValues
import android.content.Context
import com.example.chatzo.Models.usersModel
import java.util.*

class UsersDb(context: Context?) {
    var dbHelper: DbHelper?
    var context: Context?
    fun createUsers(model: usersModel) {
        try {
            val cv = ContentValues()
            cv.put(DbHelper.Companion.TABLE_USERS_COLUMN_user_id, model.id)
            cv.put(DbHelper.Companion.TABLE_USERS_COLUMN_name, model.name)
            cv.put(DbHelper.Companion.TABLE_USERS_COLUMN_profile_pic, model.profile_pic)
            dbHelper!!.writableDatabase.insert(DbHelper.Companion.TABLE_USERS, null, cv)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // System.out.println(query+" count of cursor "+cursor.getCount()
    // +"id="+ cursor.getString(0));
    val userLists: ArrayList<usersModel>
        get() {
            val products = ArrayList<usersModel>()
            val query = ("select * from "
                    + DbHelper.Companion.TABLE_USERS)
            val cursor = dbHelper!!.readableDatabase.rawQuery(query, null)
            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                do {
                    // System.out.println(query+" count of cursor "+cursor.getCount()
                    // +"id="+ cursor.getString(0));
                    val list = usersModel()
                    list.id = cursor.getString(1)
                    list.name = cursor.getString(2)
                    list.profile_pic = cursor.getString(3)
                    products.add(list)
                } while (cursor.moveToNext())
            }
            return products
        }

    fun updateNewUserId(model: usersModel) {
        try {
            val where: String = DbHelper.Companion.TABLE_USERS_COLUMN_user_id + "='" + model.id + "'"
            val cv = ContentValues()
            cv.put(DbHelper.Companion.TABLE_USERS_COLUMN_name, model.name)
            cv.put(DbHelper.Companion.TABLE_USERS_COLUMN_profile_pic, model.profile_pic)
            dbHelper!!.writableDatabase.update(DbHelper.Companion.TABLE_USERS, cv, where, null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun hasObject(id: String): Boolean {
        val selectString = "SELECT * FROM " + DbHelper.Companion.TABLE_USERS + " WHERE " + DbHelper.Companion.TABLE_USERS_COLUMN_user_id + " =?"

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