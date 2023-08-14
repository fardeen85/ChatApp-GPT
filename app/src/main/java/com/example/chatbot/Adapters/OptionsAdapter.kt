package com.example.chatbot.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.ContentInfoCompat.Flags
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.CameraActivity
import com.example.chatbot.databinding.OptionsBinding
import com.example.chatbot.models.messageModel


class OptionsAdapter(var items : ArrayList<String>,var ctx: Context) : RecyclerView.Adapter<OptionsAdapter.myViewSHolder>() {

    class myViewSHolder(var options : OptionsBinding) : RecyclerView.ViewHolder(options.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewSHolder {

        var row = OptionsBinding.inflate(LayoutInflater.from(parent.context))
        return myViewSHolder(row)
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun onBindViewHolder(holder: myViewSHolder, position: Int) {

        var listitems = items.get(position)
        holder.options.optionstext.text = listitems

        holder.options.optionstext.setOnClickListener {

            if (holder.options.optionstext.text.equals("Label image")){

                var i = Intent(ctx,CameraActivity::class.java)
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(i)
                //var model = messageModel("Sure initializing image labeler!",messageModel.SENT_BY_BOT)

            }

            if (holder.options.optionstext.text.equals("Open Camera")){

                try {
                    val intent = Intent("android.media.action.IMAGE_CAPTURE")
                    ctx.startActivity(
                        ctx.packageManager.getLaunchIntentForPackage(
                            intent.resolveActivity(ctx.packageManager).packageName
                        )
                    )
                }
                catch (e : Exception){

                    Log.d("TAG",e.message.toString())

                }

            }
        }


    }


}