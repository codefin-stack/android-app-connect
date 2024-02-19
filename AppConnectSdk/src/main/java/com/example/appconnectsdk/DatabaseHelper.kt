package com.example.appconnectsdk

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "app_connect_sdk.db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "messages"
        const val COLUMN_ID = "_id"
        const val COLUMN_CHANNEL = "channel"
        const val COLUMN_MESSAGE = "message"
        // timestamp of the expiry date-time of this message
        const val COLUMN_EXPIRY = "expiry"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = """
                CREATE TABLE $TABLE_NAME (
                    $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    $COLUMN_CHANNEL TEXT NOT NULL UNIQUE,
                    $COLUMN_MESSAGE TEXT,
                    $COLUMN_EXPIRY INTEGER
                )
            """.trimIndent()

        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }
}