package com.example.chatzo.service

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import com.example.chatzo.Activity.Incoming_call_activity
import com.example.chatzo.R
import com.example.chatzo.databases.Call_entry
import com.example.chatzo.databases.Call_logs_DB
import org.linphone.core.*
import org.linphone.core.tools.Log
import org.linphone.mediastream.Version
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class LinphoneService : Service() {
    private var mHandler: Handler? = null
    private var mTimer: Timer? = null
    var mCore: Core? = null
    private var mCoreListener: CoreListenerStub? = null
    private lateinit var mAccountCreator: AccountCreator

    // SharedPreferences call_preferences;
    var call_database: SQLiteDatabase? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        // The first call to liblinphone SDK MUST BE to a Factory method
        // So let's enable the library debug logs & log collection
        val basePath = filesDir.absolutePath
        //String basePath = getExternalFilesDir(null).getAbsolutePath();
        Factory.instance().setLogCollectionPath(basePath)
        Factory.instance().enableLogCollection(LogCollectionState.Enabled)
        Factory.instance().setDebugMode(true, getString(R.string.app_name))

        // call_preferences=getSharedPreferences("call_logs",MODE_PRIVATE);

        // Dump some useful information about the device we're running on
        Log.i(START_LINPHONE_LOGS)
        dumpDeviceInformation()
        dumpInstalledLinphoneInformation()
        mHandler = Handler()
        call_database = Call_logs_DB(this@LinphoneService).writableDatabase

        // final SharedPreferences.Editor call_editor=call_preferences.edit();
        // This will be our main Core listener, it will change activities depending on events
        mCoreListener = object : CoreListenerStub() {
            override fun onCallStateChanged(core: Core, call: Call, state: Call.State, message: String) {
                Toast.makeText(this@LinphoneService, message, Toast.LENGTH_SHORT).show()
                Log.e("state_call", state)
                if (state == Call.State.IncomingReceived) {

//                    Call call=LinphoneService.getCore().getCurrentCall();
//                    // For this sample we will automatically answer incoming calls
//                    CallParams params = LinphoneService.getCore().createCallParams(call);
//                    //params.enableVideo(true);
//                    call.acceptWithParams(params);
                    val intent = Intent(this@LinphoneService, Incoming_call_activity::class.java)
                    Toast.makeText(this@LinphoneService, "Incoming call received, answering it automatically", Toast.LENGTH_LONG).show()
                    android.util.Log.e("to_address", call.remoteAddress.username.toString())
                    val currentTime = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
                    val time = dateFormat.format(currentTime)
                    val values = ContentValues()
                    values.put(Call_entry.COLUMN_NAME_1, call.remoteAddress.username.toString())
                    values.put(Call_entry.COLUMN_NAME_2, time)
                    values.put(Call_entry.COLUMN_NAME_3, "incoming")
                    val `val` = call_database?.insert(Call_entry.TABLE_NAME, null, values)
                    if (`val`.toString().length > 0) {
                        android.util.Log.e("data_insert", "" + `val`)
                    } else {
                        android.util.Log.e("data_insert", "not inserted")
                    }
                    if (call != null) {
                        intent.putExtra("user_name", call.remoteAddress.username.toString())
                    }
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

//                    call_editor.putString("call_name",call.getToAddress().getUsername().toString());
//                    call_editor.putString("call_time",time);
//
//                    call_editor.apply();
//                    call_editor.commit();
                } else if (state == Call.State.OutgoingProgress) {
                    //state == Call.State.Connected ||
                    // This stats means the call has been established, let's start the call activity
//                    Intent intent = new Intent(LinphoneService.this, CallActivity.class);
//
//                    CallParams params = getCore().createCallParams(call);
//                    if(params!=null)
//                    {
//                        intent.putExtra("video",params.videoEnabled());
//                    }
//                    android.util.Log.e("to_address",call.getToAddress().getUsername().toString());
//
//                    Date currentTime = Calendar.getInstance().getTime();
//                    SimpleDateFormat dateFormat=new SimpleDateFormat("EEE, d MMM yyyy, h:mm a");
//                    String time=dateFormat.format(currentTime);
//
//
//                    ContentValues values=new ContentValues();
//                    values.put(Call_entry.COLUMN_NAME_1,call.getToAddress().getUsername().toString());
//                    values.put(Call_entry.COLUMN_NAME_2,time);
//                    values.put(Call_entry.COLUMN_NAME_3,"outgoing");
//
//                    long val=call_database.insert(Call_entry.TABLE_NAME,null,values);
//
//                    if(String.valueOf(val).length()>0)
//                    {
//                        android.util.Log.e("data_insert",""+val);
//                    }
//                    else {
//                        android.util.Log.e("data_insert","not inserted");
//                    }
//
////                    call_editor.putString("call_name",call.getToAddress().getUsername().toString());
////                    call_editor.putString("call_time",time);
////
////                    call_editor.apply();
////                    call_editor.commit();
//
//                   // As it is the Service that is starting the activity, we have to give this flag
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
                } else if (state == Call.State.End || state == Call.State.Released) {
                    // Once call is finished (end state), terminate the activity
                    // We also check for released state (called a few seconds later) just in case
                    // we missed the first one
                    Log.e("state_call", " finish state")
                }
            }
        }
        try {
            // Let's copy some RAW resources to the device
            // The default config file must only be installed once (the first time)
            copyIfNotExist(R.raw.linphonerc_default, "$basePath/.linphonerc")

            // The factory config is used to override any other setting, let's copy it each time
            copyFromPackage(R.raw.linphonerc_factory, "linphonerc")
        } catch (ioe: IOException) {
            Log.e(ioe)
        }

        // Create the Core and add our listener
        mCore = Factory.instance()
                .createCore("$basePath/.linphonerc", "$basePath/linphonerc", this)
        mCore!!.addListener(mCoreListener)
        // Core is ready to be configured
        configureCore()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        // If our Service is already running, no need to continue
        if (instance != null) {
            return START_STICKY
        }

        // Our Service has been started, we can keep our reference on it
        // From now one the Launcher will be able to call onServiceReady()
        instance = this

        // Core must be started after being created and configured
        mCore!!.start()

        // We also MUST call the iterate() method of the Core on a regular basis
        val lTask: TimerTask = object : TimerTask() {
            override fun run() {
                mHandler!!.post {
                    if (mCore != null) {
                        mCore!!.iterate()
                    }
                }
            }
        }
        mTimer = Timer("Linphone scheduler")
        mTimer!!.schedule(lTask, 0, 20)
        return START_STICKY
    }

    override fun onDestroy() {
        mCore!!.removeListener(mCoreListener)
        mTimer!!.cancel()
        mCore!!.stop()
        // A stopped Core can be started again
        // To ensure resources are freed, we must ensure it will be garbage collected
        mCore = null
        // Don't forget to free the singleton as well
        instance = null
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        // For this sample we will kill the Service at the same time we kill the app
        stopSelf()
        super.onTaskRemoved(rootIntent)
    }

    private fun configureCore() {
        // We will create a directory for user signed certificates if needed
        val basePath = filesDir.absolutePath
        //String basePath = getExternalFilesDir(null).getAbsolutePath();
        val userCerts = "$basePath/user-certs"
        val f = File(userCerts)
        if (!f.exists()) {
            if (!f.mkdir()) {
                Log.e("$userCerts can't be created.")
            }
        }
        mCore!!.userCertificatesPath = userCerts
    }

    private fun dumpDeviceInformation() {
        val sb = StringBuilder()
        sb.append("DEVICE=").append(Build.DEVICE).append("\n")
        sb.append("MODEL=").append(Build.MODEL).append("\n")
        sb.append("MANUFACTURER=").append(Build.MANUFACTURER).append("\n")
        sb.append("SDK=").append(Build.VERSION.SDK_INT).append("\n")
        sb.append("Supported ABIs=")
        for (abi in Version.getCpuAbis()) {
            sb.append(abi).append(", ")
        }
        sb.append("\n")
        Log.i(sb.toString())
    }

    private fun dumpInstalledLinphoneInformation() {
        var info: PackageInfo? = null
        try {
            info = packageManager.getPackageInfo(packageName, 0)
        } catch (nnfe: PackageManager.NameNotFoundException) {
            Log.e(nnfe)
        }
        if (info != null) {
            Log.i(
                    "[Service] Linphone version is ",
                    info.versionName + " (" + info.versionCode + ")")
        } else {
            Log.i("[Service] Linphone version is unknown")
        }
    }

    @Throws(IOException::class)
    private fun copyIfNotExist(ressourceId: Int, target: String) {
        val lFileToCopy = File(target)
        if (!lFileToCopy.exists()) {
            copyFromPackage(ressourceId, lFileToCopy.name)
        }
    }

    @Throws(IOException::class)
    private fun copyFromPackage(ressourceId: Int, target: String) {
        //FileOutputStream lOutputStream = openFileOutput(target, 0);
        android.util.Log.e("sample__", "target--$target")
        val file = File(target)
        val lOutputStream = FileOutputStream(file)
        val lInputStream = resources.openRawResource(ressourceId)
        var readByte: Int
        val buff = ByteArray(8048)
        while (lInputStream.read(buff).also { readByte = it } != -1) {
            lOutputStream.write(buff, 0, readByte)
        }
        lOutputStream.flush()
        lOutputStream.close()
        lInputStream.close()
    }

    public fun configureAccount(username: String?, password: String?, domain: String?) {
        // At least the 3 below values are required
        mAccountCreator = mCore!!.createAccountCreator(null)
        mAccountCreator.setUsername(username)
        mAccountCreator.setDomain(domain)
        mAccountCreator.setPassword(password)
        mAccountCreator.setTransport(TransportType.Udp)
        val transports: Transports? = LinphoneService.core?.transports
        transports?.udpPort = 23000
        LinphoneService.core?.transports = transports!!
        LinphoneService.core?.httpProxyPort = 23000


        // By default it will be UDP if not set, but TLS is strongly recommended
//        switch (mTransport.getCheckedRadioButtonId()) {
//            case R.id.transport_udp:
//                mAccountCreator.setTransport(TransportType.Udp);
//                break;
//            case R.id.transport_tcp:
//                mAccountCreator.setTransport(TransportType.Tcp);
//                break;
//            case R.id.transport_tls:
//                mAccountCreator.setTransport(TransportType.Tls);
//                break;
//        }

        //mAccountCreator.setTransport(Tr)
        // This will automatically create the proxy config and auth info and add them to the Core
        val cfg: ProxyConfig = mAccountCreator.createProxyConfig()!!
        cfg.setServerAddr("14.141.116.26:23000")
        cfg.setDialPrefix("")
        var server = cfg.serverAddr
        val serverAddr = Factory.instance().createAddress(server!!)
        if (serverAddr != null) {
            try {
                serverAddr.transport = TransportType.Udp
                server = serverAddr.asString()
                cfg.setServerAddr(server)
                //                if (mOutboundProxy.isChecked()) {
//                    mProxyConfig.setRoute(server);
//                }
                //mProxy.setValue(server);
            } catch (nfe: NumberFormatException) {
                Log.e(nfe)
            }
        }


        //cfg.setRoute("<sip:14.141.116.26:23000;transport=udp>");
        // Make sure the newly created one is the default
        LinphoneService.core?.defaultProxyConfig = cfg
    }

    companion object {
        private const val START_LINPHONE_LOGS = " ==== Device information dump ===="

        // Keep a static reference to the Service so we can access it from anywhere in the app
        var instance: LinphoneService? = null
            private set
        val isReady: Boolean
            get() = instance != null

        val core: Core?
            get() = instance?.mCore
    }
}