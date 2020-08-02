package com.example.chatzo.Models

/**
 * Created by admin on 02-Feb-18.
 */
class User {
    var mUserId: String? = null
    var mMobile: String? = null

    var pinEnter: String? = null
    fun getmMobile(): String? {
        return mMobile
    }

    fun setmMobile(mMobile: String?) {
        this.mMobile = mMobile
    }

    fun getmImei(): String? {
        return mImei
    }

    fun setmImei(mImei: String?) {
        this.mImei = mImei
    }

    var mImei: String? = null
    fun getmUserId(): String? {
        return mUserId
    }

    fun setmUserId(mUserId: String?) {
        this.mUserId = mUserId
    }
}