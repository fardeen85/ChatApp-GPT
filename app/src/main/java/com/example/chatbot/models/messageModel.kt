package com.example.chatbot.models

class messageModel {

    companion object{

        var SENT_BY_ME = "me"
        var SENT_BY_BOT = "bot"
    }
    var message : String? = null
    var sentbY:String ? = null

    constructor(message: String?, sentbY: String?) {
        this.message = message
        this.sentbY = sentbY
    }


}