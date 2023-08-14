package com.example.chatbot

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.chatbot.databinding.ActivityCameraBinding
import com.example.chatbot.viewModel.messageviewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.common.internal.ImageConvertUtils
import java.util.Locale
import java.util.Random


class CameraActivity : AppCompatActivity() {


    lateinit var cameraBinding: ActivityCameraBinding
    var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    var TAG = "TAG"
    var FILENAME_FORMAT = "dd-MMM-yyyy"
    var imageCapture:ImageCapture? = null
    lateinit var messageviewModel: messageviewModel
    var imagename = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cameraBinding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(cameraBinding.root)
        messageviewModel = messageviewModel()
        messageviewModel = ViewModelProvider(this).get(messageviewModel::class.java)


        cameraBinding.btntakephoto.setOnClickListener {
            takePhoto()
        }

        cameraBinding.btnbrowsephoto.setOnClickListener {

            var i= Intent(this@CameraActivity,ImageLabelingActivity::class.java)
            i.putExtra("image","Browse")
            startActivity(i)
        }


        LaunchCamera()
    }

    private fun startCamera() {

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)


        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(cameraBinding.previewView.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA



                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(cameraBinding.previewView.display.rotation)
                    .build()



            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,imageCapture)

            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    fun LaunchCamera() {

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){

            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, 100)

            Log.d(TAG,"Reuqsting permission")
    }
        else{

            Handler().postDelayed(object :Runnable{
                override fun run() {
                    startCamera()
                }


            },500)

        }

    }




    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case

        val generator = Random()
        val randomStringBuilder = StringBuilder()
        val randomLength: Int = generator.nextInt(5)
        var tempChar: Char
        for (i in 0 until randomLength) {
            tempChar = ((generator.nextInt(96) + 32).toChar())
            randomStringBuilder.append(tempChar)
        }

        randomStringBuilder.toString()
        val name : Any? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                .format(System.currentTimeMillis())+randomStringBuilder
        } else {

            val generator = Random()
            val randomStringBuilder = StringBuilder()
            val randomLength: Int = generator.nextInt(5)
            var tempChar: Char
            for (i in 0 until randomLength) {
                tempChar = ((generator.nextInt(96) + 32).toChar())
                randomStringBuilder.append(tempChar)
            }
            randomStringBuilder.toString()


        }

        imagename = name.toString()
        Log.d(TAG,name.toString())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name.toString())
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has

        imageCapture!!.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {

                    var i= Intent(this@CameraActivity,ImageLabelingActivity::class.java)
                    i.putExtra("image",imagename)
                    startActivity(i)
                }

                override fun onError(exception: ImageCaptureException) {
                    TODO("Not yet implemented")
                }


            }

//            @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
//             object : OnImageCapturedCallback() {
//
//                 override fun onCaptureSuccess(image: ImageProxy) {
//                     super.onCaptureSuccess(image)
//                     Log.d(TAG,"image captured")
//                     var mediaImage = image.getImage();
//                     var inputimage = InputImage.fromMediaImage(mediaImage!!, image.getImageInfo().getRotationDegrees());
//                     var bitmap = ImageConvertUtils.getInstance().getUpRightBitmap(inputimage)
//                     var i= Intent(this@CameraActivity,ImageLabelingActivity::class.java)
//                     i.putExtra("image",bitmap)
//                     startActivity(i)
////                     val yuv = YuvToRgbConverter(this@CameraActivity)
////                     image.image?.let { yuv.yuvToRgb(it,bitmap) }
//
//                 }



        )}



    fun ImageProxy.convertImageProxyToBitmap(): Bitmap {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG","ondestroy called")
    }

    override fun onBackPressed() {
        finish()
    }

     fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer[bytes]
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, null)
    }


}