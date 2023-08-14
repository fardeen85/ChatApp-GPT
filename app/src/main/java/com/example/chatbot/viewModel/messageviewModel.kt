package com.example.chatbot.viewModel


import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatbot.models.messageModel
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import java.io.IOException


class messageviewModel : ViewModel() {

     var messages : ArrayList<messageModel>
    var msgarrived = MutableLiveData<Boolean>()
    val JSON = "application/json".toMediaTypeOrNull()
    lateinit var okHttpClient: OkHttpClient
    lateinit var httpLoggingInterceptor: HttpLoggingInterceptor

    init {

        messages = ArrayList()
        httpLoggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        okHttpClient  = OkHttpClient.Builder()
            .retryOnConnectionFailure(false)
            .addInterceptor(httpLoggingInterceptor)
        .build();





    }

    public fun addtoChat(message:String, sentBy:String){


        var model = messageModel(message,sentbY = sentBy)
        messages.add(model)
        msgarrived.postValue(true)
       // livesmessages.value!!.add(model)



    }


    fun PostApi(question:String){

        viewModelScope.launch(Dispatchers.IO){
            var jsonobj = JsonObject()

            try {


                jsonobj.addProperty("model","text-davinci-003")
                jsonobj.addProperty("prompt",question)
                jsonobj.addProperty("max_tokens",3000)
                jsonobj.addProperty("temperature",0)

                var rqbody ="""
                    { "model": "text-davinci-003",
                      "prompt": "${question}",
                      "max_tokens": 4000,
                      "temperature": 0
                     }""".trimIndent()


                val body: RequestBody = jsonobj.toString().toRequestBody(JSON)
                Log.d("res",body.toString())
                val request = Request.Builder()
                    .url("https://api.openai.com/v1/completions")
                    .addHeader("Authorization","Bearer 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")
                    .addHeader("Content-Type","application/json")
                    .post(rqbody.toRequestBody("application/json".toMediaTypeOrNull()))
                    .build()

                okHttpClient.newCall(request).enqueue(object :Callback{
                    override fun onFailure(call: Call, e: IOException) {
                        addResponse("There was an error")
                        var model = messageModel("Failed to load response",sentbY = messageModel.SENT_BY_BOT)
                       // livesmessages.value!!.add(model)
                        Log.d("TAG",e.message.toString())
                    }


                    override fun onResponse(call: Call, response: Response) {

                        var responseObject: JSONObject? = null


                        if (response.isSuccessful){


                            responseObject = JSONObject(response.body!!.string())
                            Log.d("TAG",response.body.toString())
                            var arrayresponse = responseObject.getJSONArray("choices")
                            var result = arrayresponse.getJSONObject(0).getString("text")
                            addResponse(result.trim())



                        }

                        else{

                            addResponse("i am sorry but something went wrong")
                        }
                    }


                })
            }
            catch (e:Exception){

                Log.d("ex",e.message.toString())

            }


        }





    }

    fun addResponse(response:String){
        addtoChat(response,messageModel.SENT_BY_BOT)

    }

    fun Convertimage(image: Image,c:Context){

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalysis.setAnalyzer(
            ContextCompat.getMainExecutor(c)
        ) { image -> // call toBitmap function
            val bitmap: Bitmap? = toBitmap(image)
            image.close()
        }

    }

    private var bitmapBuffer: Bitmap? = null
    private fun toBitmap(image: ImageProxy): Bitmap? {
        if (bitmapBuffer == null) {
            bitmapBuffer = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        }
        bitmapBuffer!!.copyPixelsFromBuffer(image.planes[0].buffer)
        return bitmapBuffer
    }
}