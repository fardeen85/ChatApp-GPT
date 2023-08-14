package com.example.chatbot.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbot.databinding.RowBinding
import com.example.chatbot.models.messageModel

class messageAdapter(var list : ArrayList<messageModel>) : RecyclerView.Adapter<messageAdapter.myViewHolder>(){

    class myViewHolder(var row: RowBinding) : RecyclerView.ViewHolder(row.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myViewHolder {
       var row = RowBinding.inflate(LayoutInflater.from(parent.context))
        return  myViewHolder(row)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myViewHolder, position: Int) {

        var messages = list.get(position)
        if (messages.sentbY.equals(messageModel.SENT_BY_ME)){


            holder.row.leftchatview.visibility = View.GONE
            holder.row.rightchatview.visibility = View.VISIBLE
            holder.row.rightchatview.setText(messages.message)
            holder.row.usericon.visibility = View.VISIBLE
        }
        else{

            holder.row.rightchatview.visibility = View.GONE
            holder.row.leftchatview.visibility = View.VISIBLE
            holder.row.leftchatview.setText(messages.message)
            holder.row.boticon.visibility = View.VISIBLE

        }
    }
}