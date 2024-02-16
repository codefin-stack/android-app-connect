package com.example.appconnectsdk

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri

class SharedStorageProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "group.com.lhbank.profita.shared"
        const val PATH = "profita_sso_token"
        const val CONTENT_URI_STRING = "content://$AUTHORITY/$PATH"
        const val CONTENT_URI_CODE = 1

        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

        init {
            uriMatcher.addURI(AUTHORITY, PATH, CONTENT_URI_CODE)
        }
    }

    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(): Boolean {
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
        val match = uriMatcher.match(uri)
        val db = databaseHelper.writableDatabase
        val id: Long

        when (match) {
            CONTENT_URI_CODE -> {
                id = db.insert(DatabaseHelper.TABLE_NAME, null, values)
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

    private class DatabaseHelper(context: Context?) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            const val DATABASE_NAME = "my_database.db"
            const val DATABASE_VERSION = 1
            const val TABLE_NAME = "tokens"
            const val COLUMN_ID = "_id"
            const val COLUMN_TOKEN = "token"
        }

        override fun onCreate(db: SQLiteDatabase?) {
            val createTableQuery = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_TOKEN TEXT
                )
            """.trimIndent()

            db?.execSQL(createTableQuery)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
            onCreate(db)
        }
    }
}