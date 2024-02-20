package com.example.appconnectsdk

import android.net.Uri

fun getGroupId(source: String, destination: String): String {
    // Create array of string [source, destination]
    val array = arrayOf(source, destination)
    // Sort the array
    array.sort()
    // Join the array with _
    return array.joinToString("_")
}

fun getContentUri(authority: String, path: String?): Uri {
    val defaultPath  = "app_connect"
    return Uri.parse("content://$authority/${path ?: defaultPath}")
}

enum class MessageType {
    IN, OUT
}
fun getChannel(groupId: String, type: MessageType): String {
    return "${groupId}_${type}"
}

fun getExpiry(milisecond: Long = 0, second: Long = 0, minute: Long = 0, hours: Long = 0): Long {
    var ttl =  milisecond + (second * 1000) + (minute * 60 * 1000) + (hours * 60 * 60 * 1000)

    return (System.currentTimeMillis() / 1000) + ttl
}