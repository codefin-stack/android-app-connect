package com.example.appconnectsdk


// Message is a class that represents a message that can be sent to a channel.
// It contains the channel name, the message to be sent and expiry of the message.
class Message(var channel: String, var message: String, var expiry: Long) {
    companion object {
        const val CHANNEL: String = "channel"
        const val MESSAGE: String = "message"
        const val EXPIRY: String = "expiry"
    }
}