package com.example.chatzo.Activity

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.hardware.Camera.CameraInfo
import android.hardware.Camera.PictureCallback
import android.hardware.SensorManager
import android.media.CamcorderProfile
import android.media.MediaRecorder
import android.media.ThumbnailUtils
import android.os.*
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.view.View.OnLongClickListener
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.chatzo.Activity.RunTimePermission.RunTimePermissionListener
import com.example.chatzo.R
import com.example.chatzo.controllers.AppController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class WhatsappCameraActivity constructor() : AppCompatActivity(), SurfaceHolder.Callback, View.OnClickListener {
   lateinit var  surfaceHolder: SurfaceHolder
    lateinit var  camera: Camera
    private val customHandler: Handler? = Handler()
    var flag: Int = 0
    private var tempFile: File? = null
    private var jpegCallback: PictureCallback? = null
    var MAX_VIDEO_SIZE_UPLOAD: Int = 25 //MB
    override fun onResume() {
        super.onResume()
        try {
            if (myOrientationEventListener != null) myOrientationEventListener!!.enable()
        } catch (e1: Exception) {
            e1.printStackTrace()
        }
    }

    private var folder: File? = null
    public override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (runTimePermission != null) {
            runTimePermission!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.whatsapp_activity_camera)
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        AppController.camCount = 2
        runTimePermission = RunTimePermission(this)
        runTimePermission!!.requestPermission(arrayOf<String?>(Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), object : RunTimePermissionListener {
            public override fun permissionGranted() {
                // First we need to check availability of play services
                initControls()
                identifyOrientationEvents()

                //create a folder to get image
                folder = File(Environment.getExternalStorageDirectory().toString() + "/chatzo")
                if (!folder!!.exists()) {
                    folder!!.mkdirs()
                }
                //capture image on callback
                captureImageCallback()
                //
                if (camera != null) {
                    val info: CameraInfo = CameraInfo()
                    if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                        imgFlashOnOff!!.setVisibility(View.GONE)
                    }
                }
            }

            public override fun permissionDenied() {}
        })
    }

    private fun cancelSavePicTaskIfNeed() {
        if (savePicTask != null && savePicTask!!.getStatus() == AsyncTask.Status.RUNNING) {
            savePicTask!!.cancel(true)
        }
    }

    private fun cancelSaveVideoTaskIfNeed() {
        if (saveVideoTask != null && saveVideoTask!!.getStatus() == AsyncTask.Status.RUNNING) {
            saveVideoTask!!.cancel(true)
        }
    }

   lateinit private var savePicTask: SavePicTask

    private inner class SavePicTask constructor(private val data: ByteArray, rotation: Int) : AsyncTask<Void?, Void?, String?>() {
        private var rotation: Int = 0
        override fun onPreExecute() {}
        override fun onPostExecute(result: String?) {
            activeCameraCapture()
            tempFile = File(result)
            Handler().postDelayed(object : Runnable {
                public override fun run() {
                    val mIntent: Intent = Intent(this@WhatsappCameraActivity, PhotoVideoRedirectActivity::class.java)
                    mIntent.putExtra("PATH", tempFile.toString())
                    mIntent.putExtra("THUMB", tempFile.toString())
                    mIntent.putExtra("WHO", "Image")
                    startActivity(mIntent)
                }
            }, 100)
        }

        init {
            this.rotation = rotation
        }

        override fun doInBackground(vararg params: Void?): String? {
            try {
                return saveToSDCard(data, rotation)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }
    }

    @Throws(IOException::class)
    fun saveToSDCard(data: ByteArray, rotation: Int): String {
        var imagePath: String = ""
        try {
            val options: BitmapFactory.Options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeByteArray(data, 0, data.size, options)
            val metrics: DisplayMetrics = DisplayMetrics()
            getWindowManager().getDefaultDisplay().getMetrics(metrics)
            val reqHeight: Int = metrics.heightPixels
            val reqWidth: Int = metrics.widthPixels
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            val bitmap: Bitmap? = BitmapFactory.decodeByteArray(data, 0, data.size, options)
            if (rotation != 0) {
                val mat: Matrix = Matrix()
                mat.postRotate(rotation.toFloat())
                val bitmap1: Bitmap? = Bitmap.createBitmap((bitmap)!!, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mat, true)
                if (bitmap != bitmap1) {
                    bitmap.recycle()
                }
                imagePath = getSavePhotoLocal(bitmap1)
                if (bitmap1 != null) {
                    bitmap1.recycle()
                }
            } else {
                imagePath = getSavePhotoLocal(bitmap)
                if (bitmap != null) {
                    bitmap.recycle()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return imagePath
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height: Int = options.outHeight
        val width: Int = options.outWidth
        var inSampleSize: Int = 1
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round(height.toFloat() / reqHeight.toFloat())
            } else {
                inSampleSize = Math.round(width.toFloat() / reqWidth.toFloat())
            }
        }
        return inSampleSize
    }

    private fun getSavePhotoLocal(bitmap: Bitmap?): String {
        var path: String = ""
        try {
            val output: OutputStream
            val file: File = File(folder!!.getAbsolutePath(), "wc" + System.currentTimeMillis() + ".jpg")
            try {
                output = FileOutputStream(file)
                bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, output)
                output.flush()
                output.close()
                path = file.getAbsolutePath()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return path
    }

    private fun captureImageCallback() {
        surfaceHolder = imgSurface!!.getHolder()
        surfaceHolder.addCallback(this)
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        jpegCallback = object : PictureCallback {
            public override fun onPictureTaken(data: ByteArray, camera: Camera) {
                refreshCamera()
                cancelSavePicTaskIfNeed()
                savePicTask = SavePicTask(data, photoRotation)
                savePicTask!!.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
            }
        }
    }

    private inner class SaveVideoTask constructor() : AsyncTask<Void?, Void?, Void?>() {
        var thumbFilename: File? = null
        var progressDialog: ProgressDialog? = null
        override fun onPreExecute() {
            progressDialog = ProgressDialog(this@WhatsappCameraActivity)
            progressDialog!!.setMessage("Processing a video...")
            progressDialog!!.show()
            super.onPreExecute()
            imgCapture!!.setOnTouchListener(null)
            textCounter!!.setVisibility(View.GONE)
            imgSwipeCamera!!.setVisibility(View.VISIBLE)
            imgFlashOnOff!!.setVisibility(View.VISIBLE)
        }

         override fun doInBackground(vararg params: Void?): Void? {
            try {
                try {
                    myOrientationEventListener!!.enable()
                    customHandler!!.removeCallbacksAndMessages(null)
                    mediaRecorder!!.stop()
                    releaseMediaRecorder()
                    tempFile = File(folder!!.getAbsolutePath() + "/" + mediaFileName + ".mp4")
                    thumbFilename = File(folder!!.getAbsolutePath(), "t_" + mediaFileName + ".jpeg")
                    generateVideoThmb(tempFile!!.getPath(), thumbFilename)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(aVoid: Void?) {
            super.onPostExecute(aVoid)
            if (progressDialog != null) {
                if (progressDialog!!.isShowing()) {
                    progressDialog!!.dismiss()
                }
            }
            if (tempFile != null && thumbFilename != null) onVideoSendDialog(tempFile!!.getAbsolutePath(), thumbFilename!!.getAbsolutePath())
        }
    }

    private var mPhotoAngle: Int = 90
    private fun identifyOrientationEvents() {
        myOrientationEventListener = object : OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            public override fun onOrientationChanged(iAngle: Int) {
                val iLookup: IntArray = intArrayOf(0, 0, 0, 90, 90, 90, 90, 90, 90, 180, 180, 180, 180, 180, 180, 270, 270, 270, 270, 270, 270, 0, 0, 0) // 15-degree increments
                if (iAngle != ORIENTATION_UNKNOWN) {
                    val iNewOrientation: Int = iLookup.get(iAngle / 15)
                    if (iOrientation != iNewOrientation) {
                        iOrientation = iNewOrientation
                        if (iOrientation == 0) {
                            mOrientation = 90
                        } else if (iOrientation == 270) {
                            mOrientation = 0
                        } else if (iOrientation == 90) {
                            mOrientation = 180
                        }
                    }
                    mPhotoAngle = normalize(iAngle)
                }
            }
        }
        if (myOrientationEventListener!!.canDetectOrientation()) {
            myOrientationEventListener!!.enable()
        }
    }

    private var mediaRecorder: MediaRecorder? = null
    private var imgSurface: SurfaceView? = null
    private var imgCapture: ImageView? = null
    private var imgFlashOnOff: ImageView? = null
    private var imgSwipeCamera: ImageView? = null
    private var runTimePermission: RunTimePermission? = null
    private var textCounter: TextView? = null
    private var hintTextView: TextView? = null
    private fun initControls() {
        mediaRecorder = MediaRecorder()
        imgSurface = findViewById<View>(R.id.imgSurface) as SurfaceView?
        textCounter = findViewById<View>(R.id.textCounter) as TextView?
        imgCapture = findViewById<View>(R.id.imgCapture) as ImageView?
        imgFlashOnOff = findViewById<View>(R.id.imgFlashOnOff) as ImageView?
        imgSwipeCamera = findViewById<View>(R.id.imgChangeCamera) as ImageView?
        textCounter!!.setVisibility(View.GONE)
        hintTextView = findViewById<View>(R.id.hintTextView) as TextView?
        imgSwipeCamera!!.setOnClickListener(this)
        activeCameraCapture()
        imgFlashOnOff!!.setOnClickListener(this)
    }

    public override fun onBackPressed() {
        super.onBackPressed()
        AppController.camCount = 2
        cancelSavePicTaskIfNeed()
    }

    public override fun onClick(v: View) {
        when (v.getId()) {
            R.id.imgFlashOnOff -> flashToggle()
            R.id.imgChangeCamera -> {
                camera!!.stopPreview()
                camera!!.release()
                if (flag == 0) {
                    imgFlashOnOff!!.setVisibility(View.GONE)
                    flag = 1
                } else {
                    imgFlashOnOff!!.setVisibility(View.VISIBLE)
                    flag = 0
                }
                surfaceCreated((surfaceHolder)!!)
            }
            else -> {
            }
        }
    }

    private fun flashToggle() {
        if (flashType == 1) {
            flashType = 2
        } else if (flashType == 2) {
            flashType = 3
        } else if (flashType == 3) {
            flashType = 1
        }
        refreshCamera()
    }

    private fun captureImage() {
        camera!!.takePicture(null, null, jpegCallback)
        inActiveCameraCapture()
    }

    private fun releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder!!.reset() // clear recorder configuration
            mediaRecorder!!.release() // release the recorder object
            mediaRecorder = MediaRecorder()
        }
    }

    fun refreshCamera() {
        if (surfaceHolder!!.getSurface() == null) {
            return
        }
        try {
            camera!!.stopPreview()
            val param: Camera.Parameters = camera!!.getParameters()
            if (flag == 0) {
                if (flashType == 1) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO)
                    imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_auto)
                } else if (flashType == 2) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                    var params: Camera.Parameters? = null
                    if (camera != null) {
                        params = camera!!.getParameters()
                        if (params != null) {
                            val supportedFlashModes: List<String>? = params.getSupportedFlashModes()
                            if (supportedFlashModes != null) {
                                if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                                } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                    param.setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                                }
                            }
                        }
                    }
                    imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_on)
                } else if (flashType == 3) {
                    param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_off)
                }
            }
            refrechCameraPriview(param)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun refrechCameraPriview(param: Camera.Parameters) {
        try {
            camera!!.setParameters(param)
            setCameraDisplayOrientation(0)
            camera!!.setPreviewDisplay(surfaceHolder)
            camera!!.startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setCameraDisplayOrientation(cameraId: Int) {
        val info: CameraInfo = CameraInfo()
        Camera.getCameraInfo(cameraId, info)
        var rotation: Int = getWindowManager().getDefaultDisplay().getRotation()
        if (Build.MODEL.equals("Nexus 6", ignoreCase = true) && flag == 1) {
            rotation = Surface.ROTATION_180
        }
        var degrees: Int = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        camera!!.setDisplayOrientation(result)
    }

    //------------------SURFACE CREATED FIRST TIME--------------------//
    var flashType: Int = 1
    public override fun surfaceCreated(arg0: SurfaceHolder) {
        try {
            if (flag == 0) {
                camera = Camera.open(0)
            } else {
                camera = Camera.open(1)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
            return
        }
        try {
            val param: Camera.Parameters
            param = camera.getParameters()
            val sizes: List<Camera.Size> = param.getSupportedPreviewSizes()
            //get diff to get perfact preview sizes
            val displaymetrics: DisplayMetrics = DisplayMetrics()
            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics)
            val height: Int = displaymetrics.heightPixels
            val width: Int = displaymetrics.widthPixels
            val diff: Long = (height * 1000 / width).toLong()
            var cdistance: Long = Int.MAX_VALUE.toLong()
            var idx: Int = 0
            for (i in sizes.indices) {
                val value: Long = (sizes.get(i).width * 1000).toLong() / sizes.get(i).height
                if (value > diff && value < cdistance) {
                    idx = i
                    cdistance = value
                }
                Log.e("WHHATSAPP", "width=" + sizes.get(i).width + " height=" + sizes.get(i).height)
            }
            Log.e("WHHATSAPP", "INDEX:  " + idx)
            val cs: Camera.Size = sizes.get(idx)
            param.setPreviewSize(cs.width, cs.height)
            param.setPictureSize(cs.width, cs.height)
            camera.setParameters(param)
            setCameraDisplayOrientation(0)
            camera.setPreviewDisplay(surfaceHolder)
            camera.startPreview()
            if (flashType == 1) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO)
                imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_auto)
            } else if (flashType == 2) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                var params: Camera.Parameters? = null
                if (camera != null) {
                    params = camera!!.getParameters()
                    if (params != null) {
                        val supportedFlashModes: List<String>? = params.getSupportedFlashModes()
                        if (supportedFlashModes != null) {
                            if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_TORCH)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                            } else if (supportedFlashModes.contains(Camera.Parameters.FLASH_MODE_ON)) {
                                param.setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                            }
                        }
                    }
                }
                imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_on)
            } else if (flashType == 3) {
                param.setFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                imgFlashOnOff!!.setImageResource(R.drawable.ic_flash_off)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
    }

     override fun surfaceDestroyed(arg0: SurfaceHolder) {
        try {
            camera!!.stopPreview()
            camera!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

     override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {
        refreshCamera()
    }

    //------------------SURFACE OVERRIDE METHIDS END--------------------//
    private var timeInMilliseconds: Long = 0L
    private var startTime: Long = SystemClock.uptimeMillis()
    private var updatedTime: Long = 0L
    private val timeSwapBuff: Long = 0L
    private val updateTimerThread: Runnable = object : Runnable {
        public override fun run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            updatedTime = timeSwapBuff + timeInMilliseconds
            var secs: Int = (updatedTime / 1000).toInt()
            val mins: Int = secs / 60
            val hrs: Int = mins / 60
            secs = secs % 60
            textCounter!!.setText(String.format("%02d", mins) + ":" + String.format("%02d", secs))
            customHandler!!.postDelayed(this, 0)
        }
    }

    private fun scaleUpAnimation() {
        val scaleDownX: ObjectAnimator = ObjectAnimator.ofFloat(imgCapture, "scaleX", 2f)
        val scaleDownY: ObjectAnimator = ObjectAnimator.ofFloat(imgCapture, "scaleY", 2f)
        scaleDownX.setDuration(100)
        scaleDownY.setDuration(100)
        val scaleDown: AnimatorSet = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDownX.addUpdateListener(object : AnimatorUpdateListener {
            public override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                val p: View = imgCapture!!.getParent() as View
                p.invalidate()
            }
        })
        scaleDown.start()
    }

    private fun scaleDownAnimation() {
        val scaleDownX: ObjectAnimator = ObjectAnimator.ofFloat(imgCapture, "scaleX", 1f)
        val scaleDownY: ObjectAnimator = ObjectAnimator.ofFloat(imgCapture, "scaleY", 1f)
        scaleDownX.setDuration(100)
        scaleDownY.setDuration(100)
        val scaleDown: AnimatorSet = AnimatorSet()
        scaleDown.play(scaleDownX).with(scaleDownY)
        scaleDownX.addUpdateListener(object : AnimatorUpdateListener {
            public override fun onAnimationUpdate(valueAnimator: ValueAnimator) {
                val p: View = imgCapture!!.getParent() as View
                p.invalidate()
            }
        })
        scaleDown.start()
    }

    override fun onPause() {
        super.onPause()
        try {
            if (customHandler != null) customHandler.removeCallbacksAndMessages(null)
            releaseMediaRecorder() // if you are using MediaRecorder, release it first
            if (myOrientationEventListener != null) myOrientationEventListener!!.enable()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var saveVideoTask: SaveVideoTask? = null
    private fun activeCameraCapture() {
        if (imgCapture != null) {
            imgCapture!!.setAlpha(1.0f)
            imgCapture!!.setOnLongClickListener(object : OnLongClickListener {
                public override fun onLongClick(v: View): Boolean {
                    hintTextView!!.setVisibility(View.INVISIBLE)
                    try {
                        if (prepareMediaRecorder()) {
                            myOrientationEventListener!!.disable()
                            mediaRecorder!!.start()
                            startTime = SystemClock.uptimeMillis()
                            customHandler!!.postDelayed(updateTimerThread, 0)
                        } else {
                            return false
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    textCounter!!.setVisibility(View.VISIBLE)
                    imgSwipeCamera!!.setVisibility(View.GONE)
                    imgFlashOnOff!!.setVisibility(View.GONE)
                    scaleUpAnimation()
                    imgCapture!!.setOnTouchListener(object : OnTouchListener {
                        public override fun onTouch(v: View, event: MotionEvent): Boolean {
                            if (event.getAction() == MotionEvent.ACTION_BUTTON_PRESS) {
                                return true
                            }
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                scaleDownAnimation()
                                hintTextView!!.setVisibility(View.VISIBLE)
                                cancelSaveVideoTaskIfNeed()
                                saveVideoTask = SaveVideoTask()
                                saveVideoTask!!.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR)
                                return true
                            }
                            return true
                        }
                    })
                    return true
                }
            })
            imgCapture!!.setOnClickListener(object : View.OnClickListener {
                public override fun onClick(v: View) {
                    if (isSpaceAvailable) {
                        captureImage()
                    } else {
                        Toast.makeText(this@WhatsappCameraActivity, "Memory is not available", Toast.LENGTH_SHORT).show()
                    }
                }
            })
        }
    }

    fun onVideoSendDialog(videopath: String?, thumbPath: String) {
        runOnUiThread(object : Runnable {
            public override fun run() {
                if (videopath != null) {
                    val fileVideo: File = File(videopath)
                    val fileSizeInBytes: Long = fileVideo.length()
                    val fileSizeInKB: Long = fileSizeInBytes / 1024
                    val fileSizeInMB: Long = fileSizeInKB / 1024
                    if (fileSizeInMB > MAX_VIDEO_SIZE_UPLOAD) {
                        AlertDialog.Builder(this@WhatsappCameraActivity)
                                .setMessage(getString(R.string.file_limit_size_upload_format, MAX_VIDEO_SIZE_UPLOAD))
                                .setPositiveButton("OK", object : DialogInterface.OnClickListener {
                                    public override fun onClick(dialog: DialogInterface, which: Int) {
                                        dialog.dismiss()
                                    }
                                })
                                .show()
                    } else {
                        val mIntent: Intent = Intent(this@WhatsappCameraActivity, PhotoVideoRedirectActivity::class.java)
                        mIntent.putExtra("PATH", videopath.toString())
                        mIntent.putExtra("THUMB", thumbPath.toString())
                        mIntent.putExtra("WHO", "Video")
                        startActivity(mIntent)

                        //SendVideoDialog sendVideoDialog = SendVideoDialog.newInstance(videopath, thumbPath, name, phoneNuber);
                        // sendVideoDialog.show(getSupportFragmentManager(), "SendVideoDialog");
                    }
                }
            }
        })
    }

    private fun inActiveCameraCapture() {
        if (imgCapture != null) {
            imgCapture!!.setAlpha(0.5f)
            imgCapture!!.setOnClickListener(null)
        }
    }

    //--------------------------CHECK FOR MEMORY -----------------------------//
    val freeSpacePercantage: Int
        get() {
            val percantage: Int = (freeMemory() * 100 / totalMemory()).toInt()
            val modValue: Int = percantage % 5
            return percantage - modValue
        }

    fun totalMemory(): Double {
        val stat: StatFs = StatFs(Environment.getExternalStorageDirectory().getPath())
        val sdAvailSize: Double = stat.getBlockCount().toDouble() * stat.getBlockSize().toDouble()
        return sdAvailSize / 1073741824
    }

    fun freeMemory(): Double {
        val stat: StatFs = StatFs(Environment.getExternalStorageDirectory().getPath())
        val sdAvailSize: Double = stat.getAvailableBlocks().toDouble() * stat.getBlockSize().toDouble()
        return sdAvailSize / 1073741824
    }

    val isSpaceAvailable: Boolean
        get() {
            if (freeSpacePercantage >= 1) {
                return true
            } else {
                return false
            }
        }

    //-------------------END METHODS OF CHECK MEMORY--------------------------//
    private var mediaFileName: String? = null

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    protected fun prepareMediaRecorder(): Boolean {
        mediaRecorder = MediaRecorder() // Works well
        camera!!.stopPreview()
        camera!!.unlock()
        mediaRecorder!!.setCamera(camera)
        mediaRecorder!!.setVideoSource(MediaRecorder.VideoSource.CAMERA)
        mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
        if (flag == 1) {
            mediaRecorder!!.setProfile(CamcorderProfile.get(1, CamcorderProfile.QUALITY_HIGH))
        } else {
            mediaRecorder!!.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH))
        }
        mediaRecorder!!.setPreviewDisplay(surfaceHolder!!.getSurface())
        mediaRecorder!!.setOrientationHint(mOrientation)
        if (Build.MODEL.equals("Nexus 6", ignoreCase = true) && flag == 1) {
            if (mOrientation == 90) {
                mediaRecorder!!.setOrientationHint(mOrientation)
            } else if (mOrientation == 180) {
                mediaRecorder!!.setOrientationHint(0)
            } else {
                mediaRecorder!!.setOrientationHint(180)
            }
        } else if (mOrientation == 90 && flag == 1) {
            mediaRecorder!!.setOrientationHint(270)
        } else if (flag == 1) {
            mediaRecorder!!.setOrientationHint(mOrientation)
        }
        mediaFileName = "wc_vid_" + System.currentTimeMillis()
        mediaRecorder!!.setOutputFile(folder!!.getAbsolutePath() + "/" + mediaFileName + ".mp4") // Environment.getExternalStorageDirectory()
        mediaRecorder!!.setOnInfoListener(object : MediaRecorder.OnInfoListener {
            public override fun onInfo(mr: MediaRecorder, what: Int, extra: Int) {
                // TODO Auto-generated method stub
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED) {
                    val downTime: Long = 0
                    val eventTime: Long = 0
                    val x: Float = 0.0f
                    val y: Float = 0.0f
                    val metaState: Int = 0
                    val motionEvent: MotionEvent = MotionEvent.obtain(
                            downTime,
                            eventTime,
                            MotionEvent.ACTION_UP, 0f, 0f,
                            metaState
                    )
                    imgCapture!!.dispatchTouchEvent(motionEvent)
                    Toast.makeText(this@WhatsappCameraActivity, "You reached to Maximum(25MB) video size.", Toast.LENGTH_SHORT).show()
                }
            }
        })
        mediaRecorder!!.setMaxFileSize(1000 * 25 * 1000.toLong())
        try {
            mediaRecorder!!.prepare()
        } catch (e: Exception) {
            releaseMediaRecorder()
            e.printStackTrace()
            return false
        }
        return true
    }

    var myOrientationEventListener: OrientationEventListener? = null
    var iOrientation: Int = 0
    var mOrientation: Int = 90
    fun generateVideoThmb(srcFilePath: String?, destFile: File?) {
        try {
            val bitmap: Bitmap = ThumbnailUtils.createVideoThumbnail(srcFilePath, 120)
            val out: FileOutputStream = FileOutputStream(destFile)
            ThumbnailUtils.extractThumbnail(bitmap, 200, 200).compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun normalize(degrees: Int): Int {
        if (degrees > 315 || degrees <= 45) {
            return 0
        }
        if (degrees > 45 && degrees <= 135) {
            return 90
        }
        if (degrees > 135 && degrees <= 225) {
            return 180
        }
        if (degrees > 225 && degrees <= 315) {
            return 270
        }
        throw RuntimeException("Error....")
    }

    private val photoRotation: Int
        private get() {
            val rotation: Int
            val orientation: Int = mPhotoAngle
            val info: CameraInfo = CameraInfo()
            if (flag == 0) {
                Camera.getCameraInfo(0, info)
            } else {
                Camera.getCameraInfo(1, info)
            }
            if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
                rotation = (info.orientation - orientation + 360) % 360
            } else {
                rotation = (info.orientation + orientation) % 360
            }
            return rotation
        }
}