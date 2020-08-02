package com.example.chatzo.Activity

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatzo.Models.LoginResponseModel
import com.example.chatzo.Models.MessageResponseModel
import com.example.chatzo.Models.MessageRsponseDataModel
import com.example.chatzo.Models.MessagesModel
import com.example.chatzo.R
import com.example.chatzo.adapters.AdpMessagesDate
import com.example.chatzo.controllers.PathUtil
import com.example.chatzo.controllers.RetroFitClient
import com.example.chatzo.controllers.UserSessionManager
import com.example.chatzo.databases.Call_entry
import com.example.chatzo.databases.Call_logs_DB
import com.example.chatzo.databases.MessagesDateDb
import com.example.chatzo.databases.MessagesDb
import com.example.chatzo.service.LinphoneService
import com.google.gson.JsonObject
import de.hdodenhof.circleimageview.CircleImageView
import org.linphone.core.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*

class ActChatNew : AppCompatActivity() {
    lateinit var user_name: TextView
    lateinit var user_image: CircleImageView
    lateinit var back_button: ImageView
    lateinit var attachment_icon: ImageView
    var show_attachement = false
    lateinit var attachment_layout: RelativeLayout
    lateinit var cvSend: CardView
    lateinit var card_mic_button: CardView
    lateinit var mcore: Core
    var handler: Handler? = null
    var runnable: Runnable? = null
    var etMessage: EditText? = null
    var sessionManager: UserSessionManager? = null
    lateinit var chat_rv: RecyclerView
    lateinit var cvDoc: CardView
    var adpMessagesDate: AdpMessagesDate? = null
    private val PICK_PDF_REQUEST = 1
    lateinit var audioCall: ImageView
    lateinit var userId: String
    lateinit var proxyConfig: ProxyConfig
    lateinit var coreListener: CoreListener
    var outgoing_call: Boolean = false
    lateinit var call_database: SQLiteDatabase

    // private Uri filePath;
    var listSize = 0
    var DatelistSize = 0
    var messagesDateDb: MessagesDateDb? = null
    var messagesDb: MessagesDb? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_chat_new)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.action_color)
        }
        messagesDateDb = MessagesDateDb(this@ActChatNew)
        messagesDb = MessagesDb(this@ActChatNew)
        firstTime = ""
        sessionManager = UserSessionManager(this@ActChatNew)
        svChat = findViewById(R.id.svChat)
        back_button = findViewById(R.id.back_button)
        attachment_icon = findViewById(R.id.attachment_icon)
        user_image = findViewById(R.id.user_image)
        user_name = findViewById(R.id.user_name)
        attachment_icon = findViewById(R.id.attachment_icon)
        attachment_layout = findViewById(R.id.attachment_layout)
        card_mic_button = findViewById(R.id.card_mic_button)
        cvSend = findViewById(R.id.cvSend)
        etMessage = findViewById(R.id.etMessage)
        chat_rv = findViewById(R.id.chat_rv)
        cvDoc = findViewById(R.id.cvDoc)
        userId = intent.getStringExtra("id")
        if (intent.getStringExtra("imageFlag") == "") {
            user_image.setImageDrawable(resources.getDrawable(R.drawable.ic_profile_placeholder_new))
        } else {
            val byteArray = intent.getByteArrayExtra("image")
            val bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
            user_image.setImageBitmap(bmp)
        }
        user_name.setText(intent.getStringExtra("name"))
        back_button.setOnClickListener(View.OnClickListener { onBackPressed() })
        attachment_icon.setOnClickListener(View.OnClickListener {
            if (show_attachement) {
                attachment_layout.setVisibility(View.GONE)
            } else {
                attachment_layout.setVisibility(View.VISIBLE)
            }
            show_attachement = !show_attachement
        })
        cvSend.setOnClickListener(View.OnClickListener { sendMessage() })
        cvDoc.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                requestStoragePermission()
            } else {
                doRegularProcess()
            }
        })
        if (messagesDateDb!!.getUserLists(intent.getStringExtra("id")).size > 0) {
            val messageRsponseDataModels = ArrayList<MessageRsponseDataModel>()
            for (i in messagesDateDb!!.getUserLists(intent.getStringExtra("id")).indices) {
                val model = MessageRsponseDataModel()
                model.date = messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i]
                val messagesModelArrayList = ArrayList<MessagesModel>()
                for (j in messagesDb!!.getUserLists(messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i], intent.getStringExtra("id")).indices) {
                    val messagesModel = messagesDb!!.getUserLists(messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i], intent.getStringExtra("id"))[j]
                    messagesModelArrayList.add(messagesModel)
                }
                model.chats = messagesModelArrayList
                messageRsponseDataModels.add(model)
            }
            adpMessagesDate = AdpMessagesDate(messageRsponseDataModels, this@ActChatNew)
            chat_rv.setAdapter(adpMessagesDate)
            chat_rv.smoothScrollToPosition(adpMessagesDate!!.itemCount - 1)
            svChat!!.post(Runnable { svChat!!.fullScroll(View.FOCUS_DOWN) })
        } else {
        }
        if (LinphoneService.instance == null) {
            startService(Intent(this@ActChatNew, LinphoneService::class.java))
        }
        mcore = LinphoneService.core!!
        /*  proxyConfig = mcore.defaultProxyConfig!!
          if (proxyConfig != null) {
              ///     number_text.setText(proxyConfig.getContact().getUsername())
              //   sip_text.setText(proxyConfig.getDomain())
          }*/
        // Monitors the registration state of our account(s) and update the LED accordingly

        // Monitors the registration state of our account(s) and update the LED accordingly
        //
//        navigation.getMenu().findItem(R.id.change_account).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//
//                return true;
//            }
//        });


        //accountCreator =
        call_database = Call_logs_DB(this).getWritableDatabase()
        coreListener = object : CoreListenerStub() {
            override fun onRegistrationStateChanged(core: Core, cfg: ProxyConfig, state: RegistrationState, message: String) {
               Toast.makeText(this@ActChatNew,"sdsdsd"+state.toString(),Toast.LENGTH_SHORT).show();
                   if (cfg != null) {
                    //          number_text.setText(cfg.contact!!.username)
                    //        sip_text.setText(cfg.domain)
                }
            }

            override fun onCallStateChanged(core: Core, call: org.linphone.core.Call, state: org.linphone.core.Call.State, message: String) {
                // super.onCallStateChanged(core, call, state, message);
                if (state == org.linphone.core.Call.State.OutgoingProgress && !outgoing_call) {
                    //state == Call.State.Connected ||
                    // This stats means the call has been established, let's start the call activity
                    val intent = Intent(this@ActChatNew, CallActivity::class.java)
                    val params = mcore.createCallParams(call)
                    if (params != null) {
                        intent.putExtra("video", params.videoEnabled())
                    }
                    Log.e("to_address", call.toAddress.username.toString())
                    val currentTime = Calendar.getInstance().time
                    val dateFormat = SimpleDateFormat("EEE, d MMM yyyy, h:mm a")
                    val time = dateFormat.format(currentTime)
                    val values = ContentValues()
                    values.put(Call_entry.COLUMN_NAME_1, call.toAddress.username.toString())
                    values.put(Call_entry.COLUMN_NAME_2, time)
                    values.put(Call_entry.COLUMN_NAME_3, "outgoing")
                    val `val`: Long = call_database.insert(Call_entry.TABLE_NAME, null, values)
                    if (`val`.toString().length > 0) {
                        Log.e("data_insert", "" + `val`)
                    } else {
                        Log.e("data_insert", "not inserted")
                    }

//                    call_editor.putString("call_name",call.getToAddress().getUsername().toString());
//                    call_editor.putString("call_time",time);
//
//                    call_editor.apply();
//                    call_editor.commit();

                    // As it is the Service that is starting the activity, we have to give this flag
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    outgoing_call = true
                }
            }
        }
        mcore.addListener(coreListener)
        audioCall = findViewById(R.id.image_call)
        audioCall.setOnClickListener { v: View? ->
            Log.e( "onCreate: ","clicked" )
            audio_call(userId)
        }


    }

    fun audio_call(call_id: String?) {
        //mcore.addListener(mCoreListener);
        val addressToCall: Address? = call_id?.let { mcore.interpretUrl(it) };
        //
        val params: CallParams = mcore.createCallParams(null)!!
        params.enableVideo(false)
        if (addressToCall != null) {
            mcore.inviteAddressWithParams(addressToCall, params)
        }
    }

    override fun onResume() {

        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        mcore.addListener(coreListener)
        handler = Handler()
        runnable = object : Runnable {
            override fun run() {
                message
                if (etMessage!!.text.toString().trim { it <= ' ' } == "") {
                    card_mic_button!!.visibility = View.VISIBLE
                    cvSend!!.visibility = View.GONE
                } else {
                    card_mic_button!!.visibility = View.GONE
                    cvSend!!.visibility = View.VISIBLE
                }
                handler!!.postDelayed(this, 100)
            }
        }
        handler!!.postDelayed(runnable, 100)
        super.onResume()
    }

    override fun onPause() {

        // The best way to use Core listeners in Activities is to add them in onResume
        // and to remove them in onPause
        mcore.removeListener(coreListener)

        handler!!.removeCallbacks(runnable)
        super.onPause()
    }

    private fun sendMessage() {
        val `object` = JsonObject()
        `object`.addProperty("sender_id", sessionManager!!.userDetails["id"])
        `object`.addProperty("receiver_id", intent.getStringExtra("id"))
        `object`.addProperty("media_type", "Text")
        `object`.addProperty("message", etMessage!!.text.toString().trim { it <= ' ' })
        `object`.addProperty("file_name", "")
        `object`.addProperty("file_type", "")
        `object`.addProperty("file_data", "")
        Log.w("SHANTAG", "Object $`object`")
        val call = RetroFitClient
                .instance?.api!!.sendMessage(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {

            override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {

                    //  Log.w("BTAG", "Responnse" + response.body());
                    if (response.body()!!.success == "1") {
//                        if (listSize>0){
//                            listSize=listSize-1;
//                        }
                        etMessage!!.setText("")
                    } else if (response.body()!!.success == "0") {
                        Toast.makeText(this@ActChatNew, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    }
                    //  pbLoading.setVisibility(View.GONE);
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   pbLoading.setVisibility(View.GONE);
                }
            }

            override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }
    // pbLoading.setVisibility(View.GONE);

    //  Toast.makeText(ActChatNew.this, "fail", Toast.LENGTH_SHORT).show();
//   pbLoading.setVisibility(View.GONE);// Toast.makeText(ActChatNew.this,response.body().getMsg(),Toast.LENGTH_SHORT).show();
    //  pbLoading.setVisibility(View.GONE);
// adpMessagesDate.addItem(response.body().getData().get(response.body().getData().size()-1));
    // chat_rv.scrollToPosition(adpMessagesDate.getItemCount() - 1);


//                        }else if (listSize<response.body().getData().size()){
//
//                            ArrayList<MessageRsponseDataModel> arrayList=new ArrayList<>();
//                            for (int i=listSize;i<response.body().getData().size();i++){
//                              //  arrayList.add(response.body().getData().get(i).getChats());
//                            }
//
//                        }


//                            chat_rv.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    chat_rv.smoothScrollToPosition(adpMessagesDate.getItemCount()- 1);
//                                }
//                            });
//29July
    //                                adpMessagesDate=new AdpMessagesDate(response.body().getData(), ActChatNew.this);
//                                chat_rv.setAdapter(adpMessagesDate);
//                                chat_rv.smoothScrollToPosition(adpMessagesDate.getItemCount()- 1);
//                                svChat.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        svChat.fullScroll(View.FOCUS_DOWN);
//                                    }
//                                });

    //                        if (listSize==0){
    private val message: Unit
        private get() {
            val `object` = JsonObject()
            `object`.addProperty("sender_id", sessionManager!!.userDetails["id"])
            `object`.addProperty("user_id", intent.getStringExtra("id"))
            Log.w("SHANTAG", "Object $`object`")
            val call = RetroFitClient
                    .instance?.api?.receiveMessage(`object`)
            call?.enqueue(object : Callback<MessageResponseModel?> {
                override fun onResponse(call: Call<MessageResponseModel?>, response: Response<MessageResponseModel?>) {
                    try {
                        Log.w("BTAG", "Responnse" + response.body())
                        if (response.body()!!.success == "1") {
                            for (i in response.body()!!.data?.indices!!) {
                                if (response.body()!!.data?.get(i)?.date?.let { messagesDateDb!!.hasObject(it, intent.getStringExtra("id")) }!!) {
                                } else {
                                    messagesDateDb!!.createMessageDate(response.body()!!.data!![i]?.date, intent.getStringExtra("id"))
                                    for (j in response.body()!!.data?.get(i)?.chats!!.indices) {
                                        val messagesModel = response.body()!!.data?.get(i)?.chats!![j]
                                        messagesModel.dateId = response.body()!!.data!![i]?.date
                                        messagesModel.userId = intent.getStringExtra("id")
                                        if (messagesModel.id?.let { messagesDb!!.hasObject(it) }!!) {
                                        } else {
                                            messagesDb!!.createMessage(messagesModel)
                                        }
                                    }
                                }
                            }
                            if (listSize < response.body()?.data!![response.body()!!.data!!.size - 1].chats!!.size) {

                                DatelistSize = response.body()!!.data!!.size
                                //                        if (listSize==0){
                                if (firstTime == "") {
                                    firstTime = "1"

                                    //29July
                                    val messageRsponseDataModels = ArrayList<MessageRsponseDataModel>()
                                    for (i in messagesDateDb!!.getUserLists(intent.getStringExtra("id")).indices) {
                                        val model = MessageRsponseDataModel()
                                        model.date = messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i]
                                        val messagesModelArrayList = ArrayList<MessagesModel>()
                                        for (j in messagesDb!!.getUserLists(messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i], intent.getStringExtra("id")).indices) {
                                            val messagesModel = messagesDb!!.getUserLists(messagesDateDb!!.getUserLists(intent.getStringExtra("id"))[i], intent.getStringExtra("id"))[j]
                                            messagesModelArrayList.add(messagesModel)
                                        }
                                        model.chats = messagesModelArrayList
                                        messageRsponseDataModels.add(model)
                                    }
                                    adpMessagesDate = AdpMessagesDate(messageRsponseDataModels, this@ActChatNew)
                                    chat_rv!!.adapter = adpMessagesDate
                                    chat_rv!!.smoothScrollToPosition(adpMessagesDate!!.itemCount - 1)
                                    svChat!!.post { svChat!!.fullScroll(View.FOCUS_DOWN) }
                                    listSize = response.body()!!.data?.get(response.body()!!.data?.size!! - 1)?.chats?.size!!
                                    //                                adpMessagesDate=new AdpMessagesDate(response.body().getData(), ActChatNew.this);
//                                chat_rv.setAdapter(adpMessagesDate);
//                                chat_rv.smoothScrollToPosition(adpMessagesDate.getItemCount()- 1);
//                                svChat.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        svChat.fullScroll(View.FOCUS_DOWN);
//                                    }
//                                });
                                } else {
                                    firstTime = "3"
                                    chat_rv!!.post {
                                        messagesModel = response.body()!!.data?.get(response.body()!!.data?.size!! - 1)?.chats?.get(response.body()!!.data?.get(index = response.body()!!.data?.size!! - 1)?.chats?.size!! - 1)!!
                                        if (DatelistSize < response.body()?.data?.size!!) {
                                            response.body()!!.data?.get(response.body()!!.data?.size!! - 1)?.let { adpMessagesDate!!.addItem(it) }
                                            DatelistSize = response.body()!!.data?.size!!
                                        }
                                        adpMessagesDate!!.notifyDataSetChanged()
                                        if (messagesModel.id?.let { messagesDb!!.hasObject(it) }!!) {
                                        } else {
                                            messagesModel.dateId = response.body()!!.data?.get(response.body()!!.data?.size!! - 1)?.date
                                            messagesModel.userId = intent.getStringExtra("id")
                                            messagesDb!!.createMessage(messagesModel)
                                        }
                                    }

                                    // adpMessagesDate.addItem(response.body().getData().get(response.body().getData().size()-1));
                                    // chat_rv.scrollToPosition(adpMessagesDate.getItemCount() - 1);
                                    listSize = response.body()!!.data?.get(response.body()!!.data?.size!! - 1)?.chats?.size!!
                                    listSizeNew = listSize
                                }


//                        }else if (listSize<response.body().getData().size()){
//
//                            ArrayList<MessageRsponseDataModel> arrayList=new ArrayList<>();
//                            for (int i=listSize;i<response.body().getData().size();i++){
//                              //  arrayList.add(response.body().getData().get(i).getChats());
//                            }
//
//                        }


//                            chat_rv.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    chat_rv.smoothScrollToPosition(adpMessagesDate.getItemCount()- 1);
//                                }
//                            });
                            }
                            Log.w("BTAG", "listSize$listSize")
                            Log.w("BTAG", "response.body().getData().size()" + response.body()!!.data?.size)
                        } else if (response.body()!!.success == "0") {

                            // Toast.makeText(ActChatNew.this,response.body().getMsg(),Toast.LENGTH_SHORT).show();
                        }
                        //  pbLoading.setVisibility(View.GONE);
                    } catch (e: Exception) {
                        e.printStackTrace()
                        //   pbLoading.setVisibility(View.GONE);
                    }
                }

                override fun onFailure(call: Call<MessageResponseModel?>, t: Throwable) {
                    // pbLoading.setVisibility(View.GONE);

                    //  Toast.makeText(ActChatNew.this, "fail", Toast.LENGTH_SHORT).show();
                }
            })
        }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            doRegularProcess()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                //If the user has denied the permission previously your code will come to this block
                //Here you can explain why you need this permission
                //Explain here why you need this permission
            }
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }


        //And finally ask for the permission
    }

    //This method will be called when the user will tap on allow or deny
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                doRegularProcess()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun doRegularProcess() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pdf"), PICK_PDF_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_PDF_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            attachment_layout!!.visibility = View.GONE
            // Path pathToFile = Paths.get(getFileName(filePath));
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    val uri = data.data
                    val file = File(uri!!.path) //create path from uri
                    val split = file.path.split(":".toRegex()).toTypedArray() //split the path.
                    if (split.size == 1) {
                        val filePath = split[0] //assign it to a string(your choice).
                        val encoded = Files.readAllBytes(Paths.get(filePath))
                        val enc = Base64.getEncoder()
                        val strenc = enc.encode(encoded)
                        val encode = String(strenc, Charsets.UTF_8)
                        sendMessagePDf(encode, getFileName(uri))
                    } else {
                        val filePath = split[1] //assign it to a string(your choice).
                        val encoded = Files.readAllBytes(Paths.get(filePath))
                        val enc = Base64.getEncoder()
                        val strenc = enc.encode(encoded)
                        val encode = String(strenc, Charsets.UTF_8)
                        sendMessagePDf(encode, getFileName(uri))
                    }


                    //


                    // Toast.makeText(ActChatNew.this, "File " + encode, Toast.LENGTH_SHORT).show();
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                try {
                    val uri = data.data
                    val filePath = uri?.let { PathUtil.getPath(this@ActChatNew, it) }

                    // Toast.makeText(ActChatNew.this, "File " + encode, Toast.LENGTH_SHORT).show();
                } catch (e: URISyntaxException) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getFileName(uri: Uri?): String? {
        var result: String? = null
        if (uri!!.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor!!.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf('/')
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun sendMessagePDf(encodedFile: String, fileName: String?) {
        val `object` = JsonObject()
        `object`.addProperty("sender_id", sessionManager!!.userDetails["id"])
        `object`.addProperty("receiver_id", intent.getStringExtra("id"))
        `object`.addProperty("media_type", "Document")
        `object`.addProperty("message", "")
        `object`.addProperty("file_name", fileName)
        `object`.addProperty("file_type", "pdf")
        `object`.addProperty("file_data", encodedFile)
        //   Log.w("SHANTAG", "Object " + object);
        val call = RetroFitClient
                .instance?.api?.sendMessage(`object`)
        call?.enqueue(object : Callback<LoginResponseModel?> {
            override fun onResponse(call: Call<LoginResponseModel?>, response: Response<LoginResponseModel?>) {
                try {
                    Log.w("BTAG", "Responnse" + response.body()!!.success)
                    if (response.body()!!.success == "1") {
                        listSize = listSize - 1
                        etMessage!!.setText("")
                    } else if (response.body()!!.success == "0") {
                        Toast.makeText(this@ActChatNew, response.body()!!.msg, Toast.LENGTH_SHORT).show()
                    }
                    //  pbLoading.setVisibility(View.GONE);
                } catch (e: Exception) {
                    e.printStackTrace()
                    //   pbLoading.setVisibility(View.GONE);
                }
            }

            override fun onFailure(call: Call<LoginResponseModel?>, t: Throwable) {
                // pbLoading.setVisibility(View.GONE);

                //  Toast.makeText(ActChatNew.this, "fail", Toast.LENGTH_SHORT).show();
            }
        })
    }


    companion object {
        private const val STORAGE_PERMISSION_CODE = 123
        var listSizeNew = 0

        @JvmField
        var firstTime = ""

        @JvmField
        var messagesModel = MessagesModel()

        @JvmField
        var svChat: NestedScrollView? = null
    }
}