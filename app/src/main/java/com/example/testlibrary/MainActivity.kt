package com.example.testlibrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.appconnectsdk.AppConnectConfig
import com.example.appconnectsdk.AppConnectSDK
import com.example.appconnectsdk.ChannelConfiguration
import com.example.appconnectsdk.ContentProviderConfig
import com.example.appconnectsdk.getExpiry
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("MainActivity", "onCreate called")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        testSDK()
    }

    private fun testSDK() {
        try {
            val u2pChannel = AppConnectSDK.createChannel(this, "com.unicorn.provider", "com.unicorn.provider", ChannelConfiguration(commitOnRead = false))

            u2pChannel.send("Hello Profita", getExpiry(minute = 30))

            val p2uChannel = AppConnectSDK.createChannel(this, "com.unicorn.provider", "com.unicorn.provider", ChannelConfiguration(commitOnRead = false))
            var message = p2uChannel.read()
            println("Message: $message")


            message = p2uChannel.read()
            println("Message: $message")

            p2uChannel.commit()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error: ${e.message}")
        }
    }
}