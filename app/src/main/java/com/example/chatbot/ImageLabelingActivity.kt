package com.example.chatbot

import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.chatbot.databinding.ActivityImageLabelingBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File


class ImageLabelingActivity : AppCompatActivity() {

    lateinit var imagelablebinding : ActivityImageLabelingBinding
    var uri : Uri? = null
    var path:String? = null
    var bmp : Bitmap? = null
    lateinit var inputbitmap : InputImage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagelablebinding = ActivityImageLabelingBinding.inflate(layoutInflater)
        setContentView(imagelablebinding.root)


        var bundle = intent.extras
        var imagename = bundle!!.get("image")


        if (imagename!!.equals("Browse")){


             launchPhotoPicker()

        }
        else {

            Log.d("TAG", imagename.toString())

            val file = File(
                Environment.getExternalStorageDirectory(),
                "Pictures/CameraX-Image/" + imagename + ".jpg"

            )


            Glide.with(imagelablebinding.imageView2).load(file.absoluteFile).into(imagelablebinding.imageView2)
            path = file.absolutePath
            bmp = BitmapFactory.decodeFile(file.absolutePath)
            inputbitmap = InputImage.fromBitmap(bmp!!,0)
        }




        val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)



        imagelablebinding.buttonanalyse.setOnClickListener {

            inputbitmap = InputImage.fromBitmap(bmp!!,0)

            labeler.process(inputbitmap).addOnCompleteListener{

                for (label in it.result) {
                    val text = label.text
                    val confidence = label.confidence
                    val index = label.index

                    if (confidence > 0.65){

                        imagelablebinding.txtresult.let {

                            it.text = "This is a "+text
                        }
                    }


                    Log.d("TAG",text.toString())
                    Log.d("TAG",confidence.toString())
                }

            }.addOnFailureListener{

                Log.d("TAG",it.message.toString())
            }

        }

        imagelablebinding.btndelete.setOnClickListener {


            if (imagename!!.equals("Browse")) {

                var fdelete = File(uri.toString())
                if (fdelete.exists()) {

                    fdelete.delete()
                } else {

                    Toast.makeText(
                        this,
                        "Unable to delete may be the file does not exit",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {

                val file = File(
                    Environment.getExternalStorageDirectory(),
                    "Pictures/CameraX-Image/" + imagename + ".jpg"

                )

                if (file.exists()) {

                    file.delete()
                } else {

                    Toast.makeText(
                        this,
                        "Unable to delete may be the file does not exit",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }

        }

        imagelablebinding.btnbrowse.setOnClickListener {

            launchPhotoPicker()

        }



    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 12 && resultCode == RESULT_OK){


            Glide.with(imagelablebinding.imageView2).load(data!!.data).into(imagelablebinding.imageView2)
            Log.d("TAG",data.dataString.toString())
            bmp  = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data!!.data);

        }
    }

    fun launchPhotoPicker(){

        var selectintent = Intent(Intent.ACTION_GET_CONTENT)
        selectintent.type = "image/*"
        startActivityForResult(selectintent,12)
    }
}