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

fun getContentUri(authority: String, path: String = "app_connect"): Uri {
    return Uri.parse("content://$authority/$path")
}

fun getChannel(groupId: String, type: String): String {
    return "${groupId}_${type}"
}

fun getExpiry(milisecond: Long = 0, second: Long = 0, minute: Long = 0, hours: Long = 0): Long {
    var ttl =  milisecond + (second * 1000) + (minute * 60 * 1000) + (hours * 60 * 60 * 1000)

    return (System.currentTimeMillis() / 1000) + ttl
}