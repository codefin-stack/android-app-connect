package com.example.appconnectsdk

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.appconnectsdk.test", appContext.packageName)

//        AppConnectConfig.initialize(object: ContentProviderConfig {
//            override val contentProviderAuthority = "com.example.testlibrary.provider"
//            override val contentProviderPath = "messages"
//        })
//
//        val u2pChannel = AppConnectSDK.createChannel(appContext, "com.example.testlibrary.provider", "com.example.testlibrary.provider", ChannelConfiguration(commitOnRead = false))
//
//        u2pChannel.send("Hello Profita", getExpiry(minute = 30))
//
//        val p2uChannel = AppConnectSDK.createChannel(appContext, "com.example.testlibrary.provider", "com.example.testlibrary.provider", ChannelConfiguration(commitOnRead = false))
//        var message = p2uChannel.read()
//        println("Message: $message")
//        p2uChannel.commit()
//
//        assertEquals("Hello Profita", message)
    }
}