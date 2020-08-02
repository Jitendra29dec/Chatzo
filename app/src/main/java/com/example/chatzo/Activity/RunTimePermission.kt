package com.example.chatzo.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.chatzo.R

/**
 * The Run Time Permission
 *
 * @author The MIT License (MIT)
 *
 *
 * Copyright (c) 2016 Parag Ghetiya & Chintan Khetiya
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class RunTimePermission constructor(private val activity: Activity) : Activity() {
    private var arrayListPermission: ArrayList<PermissionBean>? = null
    var arrayPermissions: ArrayList<String> = ArrayList()
    private var runTimePermissionListener: RunTimePermissionListener? = null

    inner class PermissionBean constructor() {
        var permission: String? = null
        var isAccept: Boolean = false
    }

    fun requestPermission(permissions: Array<String?>, runTimePermissionListener: RunTimePermissionListener?) {
        this.runTimePermissionListener = runTimePermissionListener
        arrayListPermission = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (i in permissions.indices) {
                val permissionBean: PermissionBean = PermissionBean()
                if (ContextCompat.checkSelfPermission(activity, (permissions.get(i))!!) == PackageManager.PERMISSION_GRANTED) {
                    permissionBean.isAccept = true
                } else {
                    permissionBean.isAccept = false
                    permissionBean.permission = permissions.get(i)
                    arrayListPermission!!.add(permissionBean)
                }
            }
            if (arrayListPermission!!.size <= 0) {
                runTimePermissionListener!!.permissionGranted()
                return
            }
            for (i in arrayListPermission!!.indices) {
                arrayPermissions!![i] = arrayListPermission!!.get(i).permission.toString()
            }

            activity.requestPermissions(arrayPermissions.toArray() as Array<out String>, 10)
        } else {
            if (runTimePermissionListener != null) {
                runTimePermissionListener.permissionGranted()
            }
        }
    }

    open interface RunTimePermissionListener {
        fun permissionGranted()
        fun permissionDenied()
    }

    private fun callSettingActivity() {
        val intent: Intent = Intent()
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", activity.getPackageName(), null)
        intent.setData(uri)
        activity.startActivity(intent)
    }

    private fun checkUpdate() {
        var isGranted: Boolean = true
        var deniedCount: Int = 0
        for (i in arrayListPermission!!.indices) {
            if (!arrayListPermission!!.get(i).isAccept) {
                isGranted = false
                deniedCount++
            }
        }
        if (isGranted) {
            if (runTimePermissionListener != null) {
                runTimePermissionListener!!.permissionGranted()
            }
        } else {
            if (runTimePermissionListener != null) {
//                if (deniedCount == arrayListPermission.size())
//                {
                setAlertMessage()

//                }
                runTimePermissionListener!!.permissionDenied()
            }
        }
    }

    fun setAlertMessage() {
        val adb: AlertDialog.Builder
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            adb = AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert)
        } else {
            adb = AlertDialog.Builder(activity)
        }
        adb.setTitle(activity.getResources().getString(R.string.app_name))
        val msg: String = ("<p>Dear User, </p>" +
                "<p>Seems like you have <b>\"Denied\"</b> the minimum requirement permission to access more features of application.</p>" +
                "<p>You must have to <b>\"Allow\"</b> all permission. We will not share your data with anyone else.</p>" +
                "<p>Do you want to enable all requirement permission ?</p>" +
                "<p>Go To : Settings >> App > " + activity.getResources().getString(R.string.app_name) + " Permission : Allow ALL</p>")
        adb.setMessage(Html.fromHtml(msg))
        adb.setPositiveButton("Allow All", object : DialogInterface.OnClickListener {
            public override fun onClick(dialog: DialogInterface, which: Int) {
                callSettingActivity()
                dialog.dismiss()
            }
        })
        adb.setNegativeButton("Remind Me Later", object : DialogInterface.OnClickListener {
            public override fun onClick(dialog: DialogInterface, which: Int) {
                dialog.dismiss()
            }
        })
        if (!activity.isFinishing() && msg.length > 0) {
            adb.show()
        } else {
            Log.v("log_tag", "either activity finish or message length is 0")
        }
    }

    private fun updatePermissionResult(permissions: String, grantResults: Int) {
        for (i in arrayListPermission!!.indices) {
            if ((arrayListPermission!!.get(i).permission == permissions)) {
                if (grantResults == 0) {
                    arrayListPermission!!.get(i).isAccept = true
                } else {
                    arrayListPermission!!.get(i).isAccept = false
                }
                break
            }
        }
    }

    public override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        for (i in permissions.indices) {
            updatePermissionResult(permissions.get(i), grantResults.get(i))
        }
        checkUpdate()
    }

}