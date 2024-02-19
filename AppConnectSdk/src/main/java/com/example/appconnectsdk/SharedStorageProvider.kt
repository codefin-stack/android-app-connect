package com.example.appconnectsdk

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

class SharedStorageProvider : ContentProvider() {

    var authority = "";
    var path = "";
    var uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
    val CONTENT_URI_CODE = 1

//    companion object {
////        const val AUTHORITY = "group.com.lhbank.profita.shared"
////        private const val AUTHORITY = BuildConfig.LIBRARY_PACKAGE_NAME + ".provider"
////        AppConnectSDK.getContentProviderAuthority()
////        private const val PATH = "app_connect"
//        const val CONTENT_URI_CODE = 1
//
////        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
//
////        init {
////            uriMatcher.addURI(AUTHORITY, PATH, CONTENT_URI_CODE)
////        }
//    }

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(): Boolean {
        /// Add URI Matcher
        authority = "com.example.testlibrary.provider" //AppConnectConfig.getContentProviderAuthority()
        path = "app_connect" //AppConnectConfig.getContentProviderPath()
        uriMatcher.addURI(authority, path, CONTENT_URI_CODE)

        /// Create Database
        databaseHelper = DatabaseHelper(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? {
        val match = uriMatcher.match(uri)
        val db = databaseHelper.readableDatabase
        val cursor: Cursor?

        when (match) {
            CONTENT_URI_CODE -> {
                cursor = db.query(
                    DatabaseHelper.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        cursor?.setNotificationUri(context?.contentResolver, uri)
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (values == null) {
            throw AssertionError("Content value must not null")
        }

        if (!values.containsKey("message") || !values.containsKey("channel") || !values.containsKey("expiry")) {
            throw AssertionError("Content value must contain message, channel and expiry")
        }

        val message = values.get("message") as String
        val channel = values.get("channel") as String
        val expiry = values.get("expiry") as Long

        val insertValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CHANNEL, channel)
            put(DatabaseHelper.COLUMN_MESSAGE, message)
            put(DatabaseHelper.COLUMN_EXPIRY, expiry)
        }

        val match = uriMatcher.match(uri)
        val db = databaseHelper.writableDatabase
        val id: Long


        when (match) {
            CONTENT_URI_CODE -> {
                id = db.insertWithOnConflict(DatabaseHelper.TABLE_NAME, null, insertValues, SQLiteDatabase.CONFLICT_REPLACE)
                if (id != -1L) {
                    context?.contentResolver?.notifyChange(uri, null)
                    return ContentUris.withAppendedId(uri, id)
                }
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val match = uriMatcher.match(uri)
        val db = databaseHelper.writableDatabase
        val rowsUpdated: Int

        when (match) {
            CONTENT_URI_CODE -> {
                rowsUpdated = db.update(
                    DatabaseHelper.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (rowsUpdated != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return rowsUpdated
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val match = uriMatcher.match(uri)
        val db = databaseHelper.writableDatabase
        val rowsDeleted: Int

        when (match) {
            CONTENT_URI_CODE -> {
                rowsDeleted = db.delete(
                    DatabaseHelper.TABLE_NAME,
                    selection,
                    selectionArgs
                )
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

        if (rowsDeleted != 0) {
            context?.contentResolver?.notifyChange(uri, null)
        }

        return rowsDeleted
    }

}