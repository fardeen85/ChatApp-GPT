package com.example.chatbot

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatbot.Adapters.OptionsAdapter
import com.example.chatbot.Adapters.messageAdapter
import com.example.chatbot.databinding.ActivityMainBinding
import com.example.chatbot.models.messageModel
import com.example.chatbot.viewModel.messageviewModel


class MainActivity : AppCompatActivity() {

    lateinit var messageAdapter: messageAdapter
    lateinit var viewmodel:messageviewModel
    lateinit var mainActivitybinding : ActivityMainBinding
    lateinit var optionsAdapter: OptionsAdapter
    var REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    var TAG = "TAG"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivitybinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainActivitybinding.root)
        viewmodel = ViewModelProvider(this).get(messageviewModel::class.java)
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        var newheight = height-(height/2)

        mainActivitybinding.msgrecycler.minimumHeight = newheight

        Log.d("h", height.toString())


        var list = ArrayList<String>()
        list.add("Open Camera")
        list.add("Label image")
        list.add("Send message to a contact")

        optionsAdapter = OptionsAdapter(list,this)
        mainActivitybinding.optionsrecycler.adapter = optionsAdapter

        mainActivitybinding.btnsend.setOnClickListener {


            viewmodel.addtoChat(mainActivitybinding.editextmsg.text.toString(),messageModel.SENT_BY_ME)
            mainActivitybinding.welcometext.visibility = View.GONE
            messageAdapter.notifyDataSetChanged()
            Log.d("TAG","click wokred")
            viewmodel.PostApi(mainActivitybinding.editextmsg.text.toString())
            mainActivitybinding.editextmsg.setText("")
            messageAdapter = messageAdapter(viewmodel.messages)
            mainActivitybinding.msgrecycler.adapter = messageAdapter



        }

        var layoutmanager  = LinearLayoutManager(this)
        layoutmanager.stackFromEnd = true
        mainActivitybinding.msgrecycler.layoutManager = layoutmanager
        messageAdapter = messageAdapter(viewmodel.messages)
        mainActivitybinding.msgrecycler.adapter = messageAdapter


//        viewmodel.livesmessages.observe(this,object :Observer<ArrayList<messageModel>>{
//            override fun onChanged(t: ArrayList<messageModel>?) {
//                if (t != null){
//
//                    messageAdapter = messageAdapter(t)
//                    mainActivitybinding.msgrecycler.adapter = messageAdapter
//
//                }
//            }
//
//
//
//
//        })


        viewmodel.msgarrived.observe(this,object :Observer<Boolean>{
            override fun onChanged(t: Boolean?) {
                if (t!!){

                    messageAdapter = messageAdapter(viewmodel.messages)
                    mainActivitybinding.msgrecycler.adapter = messageAdapter

                }
            }




        })



    }


    fun Sendmessages(){


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }







}