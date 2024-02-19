package com.example.appconnectsdk

import android.content.Context
import android.net.Uri
import android.util.Log
import java.util.Objects

object AppConnectConfig {
    private var config: ContentProviderConfig = DefaultMyLibraryConfig

    fun initialize(config: ContentProviderConfig) {
        this.config = config
    }

    fun getContentProviderAuthority(): String {
        return config.contentProviderAuthority
    }

    fun getContentProviderPath(): String {
        return config.contentProviderPath
    }
}

class AppConnectSDK(private val source: String, private val destination: String, private val config: ChannelConfiguration, private val groupId: String) {
    private var context: Context? = null
//    private var source: String = ""
//    private var destination: String = ""
//    private var groupId = ""
//    private var config: ChannelConfiguration? = null

    companion object {
        fun createChannel(context: Context, source: String, destination: String, config: ChannelConfiguration):AppConnectSDK {
            return AppConnectSDK(source, destination, config, getGroupId(source, destination)).apply {
                this.context = context
            }
        }

    }

    fun send(message: String, expiry: Long?) {
        if (context == null) {
            throw Exception("Channel not created")
        }
        val defaultExpiry: Long = (System.currentTimeMillis() / 1000) + 1800
        SharedStorageResolver(context!!)
            .sendMessage(
                getContentUri(source),
                Message(getChannel(groupId, "out"), message, expiry ?: defaultExpiry)
            )
    }

    fun read(): String {
        if (context == null) {
            throw Exception("Channel not created")
        }

        val incomingMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(destination),
            getChannel(groupId, "out")
        )
        val readedMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(source),
            getChannel(groupId, "in")
        )

        Log.d("AppConnectSDK", "incommingMessage :${incomingMessage}")
        Log.d("AppConnectSDK", "readedMessage :${readedMessage}")

        if (incomingMessage == null) {
            throw Exception("Message not found")
        }

        if (readedMessage != null && readedMessage.message == incomingMessage.message) {
            throw Exception("Message already read")
        }

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
        val incomingMessage = SharedStorageResolver(context!!).readMessage(
            getContentUri(destination),
            getChannel(groupId, "out")
        ) ?: return

        SharedStorageResolver(context!!)
            .sendMessage(
                getContentUri(source),
                Message(getChannel(groupId, "in"), incomingMessage.message, 0)
            )
    }
}

class ChannelConfiguration(val commitOnRead: Boolean) {}

interface ContentProviderConfig {
    val contentProviderAuthority: String
    val contentProviderPath: String
}

object DefaultMyLibraryConfig : ContentProviderConfig {
    override val contentProviderAuthority = "com.example.mylibrary.provider"
    override val contentProviderPath = "messages"
}