package com.example.appconnectsdk

import android.content.Context
import android.net.Uri
import android.util.Log
import java.util.Objects

object AppConnectConfig {
    private var config: ContentProviderConfig = DefaultMyLibraryConfig

    fun getContentProviderPath(): String {
        return config.contentProviderPath ?: DefaultMyLibraryConfig.contentProviderPath
    }
}

class AppConnectSDK(private val source: String, private val destination: String, private val config: ChannelConfiguration, private val groupId: String) {
    private var context: Context? = null

    companion object {
        fun createChannel(context: Context, source: String, destination: String, config: ChannelConfiguration):AppConnectSDK {
            return AppConnectSDK(source, destination, config, getGroupId(source, destination)).apply {
                this.context = context
            }
        }

    }

    fun send(message: String, expiry: Long?) {
        Log.d("AppConnectSDK", "[send] called with >>>>> Message: $message, expiry: $expiry")
        if (context == null) {
            throw Exception("Channel not created")
        }
        val defaultExpiry: Long = (System.currentTimeMillis() / 1000) + 1800
        SharedStorageResolver(context!!)
            .sendMessage(
                getContentUri(source, config.destinationPath),
                Message(getChannel(groupId, MessageType.OUT), message, expiry ?: defaultExpiry)
            )
    }

    fun read(): String {
        Log.d("AppConnectSDK", "[read] called")
        if (context == null) {
            throw Exception("Channel not created")
        }

        val incomingMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(destination, config.destinationPath),
            getChannel(groupId, MessageType.OUT)
        )
        val readedMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(source, AppConnectConfig.getContentProviderPath()),
            getChannel(groupId, MessageType.IN)
        )

        Log.d("AppConnectSDK", "[read] incommingMessage :${incomingMessage}")
        Log.d("AppConnectSDK", "[read] readedMessage :${readedMessage}")

        Log.d("AppConnect", "[read] checking null incoming message")
        if (incomingMessage == null) {
            throw Exception("Message not found")
        }

        Log.d("AppConnect", "[read] checking already read")
        if (readedMessage != null && readedMessage.message == incomingMessage.message) {
            throw Exception("Message already read")
        }

        Log.d("AppConnect", "[read] checking expired")
        val currentTime = System.currentTimeMillis() / 1000
        if (currentTime > incomingMessage.expiry) {
            Log.d("AppConnectSDK", "[read] currentTime: $currentTime")
            Log.d("AppConnectSDK", "[read] incommingMessage.expiry :${incomingMessage.expiry}")
            throw Exception("Message has expired")
        }

        if (config?.commitOnRead == true) {
            this.commit()
        }
        return incomingMessage.message
    }

    fun commit() {
        Log.d("AppConnectSDK", "[commit] called")
        val incomingMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(destination, config.destinationPath),
            getChannel(groupId, MessageType.OUT)
        ) ?: return

        SharedStorageResolver(context!!)
            .sendMessage(
                getContentUri(source, AppConnectConfig.getContentProviderPath()),
                Message(getChannel(groupId, MessageType.IN), incomingMessage.message, 0)
            )
    }
}

class ChannelConfiguration(val commitOnRead: Boolean, val destinationPath: String = "app_connect") {}

interface ContentProviderConfig {
    val contentProviderAuthority: String
    val contentProviderPath: String?
        get() = "app_connect"
}

object DefaultMyLibraryConfig : ContentProviderConfig {
    override val contentProviderAuthority = "com.example.mylibrary.provider"
    override val contentProviderPath = "app_connect"
}