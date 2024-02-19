package com.example.appconnectsdk

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.net.Uri
import android.util.Log

class SharedStorageResolver(context: Context) {
//    private val contentResolver: ContentResolver = context.contentResolver
    private val contentResolver: ContentResolver = context.contentResolver // getApplicationContext().contentResolver

    fun sendMessage(uri: Uri, message: Message): Uri? {
        val values = ContentValues().apply {
            put(Message.CHANNEL, message.channel)
            put(Message.MESSAGE, message.message)
            put(Message.EXPIRY, message.expiry)
        }
        Log.d("SharedStorageResolver", "[sendMessage] uri >>>>>> $uri")
        Log.d("SharedStorageResolver", "[sendMessage] values >>>>>> $values")
        Log.d("SharedStorageResolver", "[sendMessage] message.channel >>>>>> ${message.channel}")
        Log.d("SharedStorageResolver", "[sendMessage] message.message >>>>>> ${message.message}")
        Log.d("SharedStorageResolver", "[sendMessage] message.expiry >>>>>> ${message.expiry}")
        return contentResolver.insert(uri, values)
    }

    fun readMessage(uri: Uri, channel: String): Message? {
        val projection = arrayOf(Message.CHANNEL, Message.MESSAGE, Message.EXPIRY)
        val selection = "${Message.CHANNEL} = ?"
        val selectionArgs = arrayOf(channel)

        Log.d("SharedStorageResolver", "[readMessage] uri >>>>>> $uri")
        Log.d("SharedStorageResolver", "[readMessage] projection >>>>>> $projection")
        Log.d("SharedStorageResolver", "[readMessage] selection >>>>>> $selection")

        val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)
        var message: Message? = null
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()
                val channelIndex = cursor.getColumnIndex(Message.CHANNEL)
                val messageIndex = cursor.getColumnIndex(Message.MESSAGE)
                val expiryIndex = cursor.getColumnIndex(Message.EXPIRY)
                if (channelIndex != -1 && messageIndex != -1 && expiryIndex != -1) {
                    message = Message(
                        cursor.getString(channelIndex),
                        cursor.getString(messageIndex),
                        cursor.getLong(expiryIndex)
                    )
                }
            } else {
                message = null
            }
            cursor.close()
        }

        Log.d("SharedStorageResolver", "[readMessage] message >>>>>> $message")
        Log.d("SharedStorageResolver", "[readMessage] message.message >>>>>> ${message?.message}")
        Log.d("SharedStorageResolver", "[readMessage] message.channel >>>>>> ${message?.channel}")
        Log.d("SharedStorageResolver", "[readMessage] message.expiry >>>>>> ${message?.expiry}")
        return message
    }

//    fun insertToken(contentProviderUri: String, token: String): Uri? {
//        val values = ContentValues().apply {
//            put("token", token)
//        }
//        val uri = Uri.parse(contentProviderUri) // Replace with your authority and path
//        return contentResolver.insert(uri, values)
//    }
//
//    fun updateToken(contentProviderUri: String, token: String): Int {
//        val values = ContentValues().apply {
//            put("token", token)
//        }
//        val uri = Uri.parse(contentProviderUri) // Replace with your authority and path
//        return contentResolver.update(uri, values, null, null)
//    }
//    fun queryToken(contentProviderUri: String): String? {
//        val uri = Uri.parse(contentProviderUri) // Replace with your authority and path
//        val projection = arrayOf("token")
//        Log.d("MyContentResolver", "uri >>>>>> $uri")
//        Log.d("MyContentResolver", "projection >>>>>> $projection")
//        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
//        var token: String? = null
//        if (cursor != null) {
//            if (cursor.count > 0) {
//                cursor.moveToFirst()
//                val colIndex = cursor.getColumnIndex("token")
//                if (colIndex != -1) {
//                    token = cursor.getString(colIndex)
//                }
//            } else {
//                token = null
//            }
//            cursor.close()
//        }
////        cursor?.use {
////            if (it.count > 0) {
////                it.moveToFirst()
////                val colIndex = it.getColumnIndex("token")
////                if (colIndex != -1) {
////                    token = it.getString(colIndex)
////                }
////            } else {
////                token = null
////            }
////        }
//        return token
//    }
}