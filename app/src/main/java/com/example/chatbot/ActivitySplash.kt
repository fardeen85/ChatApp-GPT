package com.example.chatbot

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.chatbot.databinding.ActivitySplashBinding

class ActivitySplash : AppCompatActivity() {

    lateinit var splashBinding:ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        Handler().postDelayed(object :Runnable{
            override fun run() {

                var i= Intent(this@ActivitySplash,MainActivity::class.java)
                startActivity(i)
                finish()
            }


        },1500)


    }
}