package com.example.appconnectsdk

import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import android.util.Log

class SharedStorageProvider : ContentProvider() {

    private var authority = "";
    private var path = "";
    private var uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    companion object {
        const val CONTENT_URI_CODE = 1
    }

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(): Boolean {
        Log.d("SharedStorageProvider", "[onCreate] called")
        /// Add URI Matcher
//        Log.d("SharedStorageProvider", "[onCreate] authority 1: com.example.testlibrary.provider")
//        Log.d("SharedStorageProvider", "[onCreate] authority 2: ${AppConnectConfig.getContentProviderAuthority()}")
//        Log.d("SharedStorageProvider", "[onCreate] is same : ${"com.example.testlibrary.provider" == AppConnectConfig.getContentProviderAuthority()}")
        val providerInfo = context!!.packageManager.getProviderInfo(ComponentName(context!!, SharedStorageProvider::class.java), PackageManager.GET_META_DATA)
        val metaAuthurity = providerInfo.metaData.getString("content_provider_authority")
        Log.d("SharedStorageProvider", "[onCreate] authority from meta-data: $metaAuthurity")

        authority = metaAuthurity ?: "com.example.testlibrary.provider" //AppConnectConfig.getContentProviderAuthority()

        path = "app_connect" //AppConnectConfig.getContentProviderPath()
        Log.d("SharedStorageProvider", "[onCreate] authority: $authority")
        Log.d("SharedStorageProvider", "[onCreate] path: $path")

        uriMatcher.addURI(authority, path, CONTENT_URI_CODE)

        val testUri = Uri.parse("content://$authority/$path")
        val match = uriMatcher.match(testUri)
        Log.d("SharedStorageProvider", "[onCreate] match with $testUri is $match")
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
        Log.d("SharedStorageProvider", "[query] called with >>>>> uri: $uri, projection: $projection, selection: $selection, selectionArgs: $selectionArgs, sortOrder: $sortOrder")
        val match = uriMatcher.match(uri)
        Log.d("SharedStorageProvider", "[query] match >>>>> $match")
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
        Log.d("SharedStorageProvider", "[insert] called with >>>>> uri: $uri, values: $values")
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
        Log.d("SharedStorageProvider", "[insert] match >>>>> $match")
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