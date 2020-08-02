package com.example.chatzo.controllers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import com.example.chatzo.MainActivity
import com.example.chatzo.Models.User
import java.util.*

/**
 * Created by admin on 02-Feb-18.
 */
class UserSessionManager(context: Context?) {
    var sharedPreferences: SharedPreferences
    var editor: SharedPreferences.Editor
    var context: Context?

    // SHARED PREFERENCE MODE
    var PRIVATE_MODE = 0

    // CREATE LOGIN SESSION
    fun createUserLoginSession(user: User) {
        editor.putBoolean(IS_USER_LOGIN, true)
        editor.putString(KEY_USER_ID, user.getmUserId())
        editor.putString(KEY_USER_PIN, user.pinEnter)
        editor.commit()
    }

    fun updatePin(pin: String?) {
        editor.putString(KEY_USER_PIN, pin)
        editor.commit()
    }

    //CHECK LOGIN WILL CHECK USER LOGIN STATUS IF FALSE IT WILL REDIRECT TO LOGIN PAGE ELSE DO ANYTHING
    fun checkLogin(): Boolean {
        //Check login1 status
        return if (isUserLoggedIn) {
            //Member is not logged in redirect him to login1 activity
            //Intent intent = new Intent(context, ActNavigation.class);
            // Closing all the Activities from stack
            //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // Add new Flag to start new Activity
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //context.startActivity(intent);
            true
        } else false
    }

    // CHECK FOR LOGIN
    val isUserLoggedIn: Boolean
        get() {
            Log.d("TAG", "USER SESSION MANAGER IS USER LOGIN " + sharedPreferences.getBoolean(IS_USER_LOGIN, false))
            return sharedPreferences.getBoolean(IS_USER_LOGIN, false)
        }

    val userDetails: HashMap<String, String?>
        get() {
            val user = HashMap<String, String?>()
            user[KEY_USER_ID] = sharedPreferences.getString(KEY_USER_ID, null)
            user[KEY_USER_PIN] = sharedPreferences.getString(KEY_USER_PIN, null)
            return user
        }

    //LOGOUT USER
    fun logout() {
        //Clearing all user data from shared preferences
        editor.clear()
        editor.commit()
        //
        val intent = Intent(context, MainActivity::class.java)
        // Closing all the Activities from stack
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Add new Flag to start new Activity
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context!!.startActivity(intent)
    }

    companion object {
        private const val IS_USER_LOGIN = "IsUserLoggedIn"
        const val KEY_USER_ID = "id"
        const val KEY_USER_PIN = "pin"

        // SHARED PREFERENCE FILENAME
        private const val PREF_NAME = "ShaanTBD"
    }

    init {
        sharedPreferences = context!!.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = sharedPreferences.edit()
        this.context = context
    }
}