package com.example.myprojetsynthese2

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// Database helper class to manage SQLite database creation and version management.
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "MediCalc.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_ACCOUNT = "Account"
        const val COLUMN_ID = "id"
        const val COLUMN_EMAIL = "email"
        const val COLUMN_PASSWORD = "password"
        const val COLUMN_FIRST_NAME = "first_name"
        const val COLUMN_LAST_NAME = "last_name"

        const val TABLE_LOG = "Log"
        const val COLUMN_LOG_ID = "id"
        const val COLUMN_ACCOUNT_ID = "account_id"
        const val COLUMN_SLEEP_SCHEDULE = "sleep_schedule"
        const val COLUMN_FAT_PERCENTAGE = "fat_percentage"
        const val COLUMN_IDEAL_WEIGHT = "ideal_weight"
        const val COLUMN_BODY_MASS_INDEX = "body_mass_index"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    // Called when the database is created for the first time.
    override fun onCreate(db: SQLiteDatabase) {
        val createAccountTable = """
            CREATE TABLE $TABLE_ACCOUNT (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_EMAIL TEXT NOT NULL UNIQUE,
                $COLUMN_PASSWORD TEXT NOT NULL,
                $COLUMN_FIRST_NAME TEXT NOT NULL,
                $COLUMN_LAST_NAME TEXT NOT NULL
            );
        """.trimIndent()
        db.execSQL(createAccountTable)

        val createLogTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_LOG (
                $COLUMN_LOG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ACCOUNT_ID INTEGER NOT NULL,
                $COLUMN_SLEEP_SCHEDULE TEXT,
                $COLUMN_FAT_PERCENTAGE REAL,
                $COLUMN_IDEAL_WEIGHT REAL,
                $COLUMN_BODY_MASS_INDEX REAL,
                $COLUMN_TIMESTAMP DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY ($COLUMN_ACCOUNT_ID) REFERENCES $TABLE_ACCOUNT($COLUMN_ID)
            );
        """.trimIndent()
        db.execSQL(createLogTable)
    }

    // Called when the database needs to be upgraded.
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ACCOUNT")
        onCreate(db)
    }


    // Insert a new account into the Account table.
    fun insertAccount(email: String, password: String, firstName: String, lastName: String): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_FIRST_NAME, firstName)
            put(COLUMN_LAST_NAME, lastName)
        }
        val result = db.insert(TABLE_ACCOUNT, null, values)
        db.close()
        return result != -1L
    }

    // Insert a new log entry into the Log table.
    fun insertLog(accountId: Int, sleepSchedule: String?, fatPercentage: Double?, idealWeight: Double?, bodyMassIndex: Double?): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACCOUNT_ID, accountId)
            put(COLUMN_SLEEP_SCHEDULE, sleepSchedule)
            put(COLUMN_FAT_PERCENTAGE, fatPercentage)
            put(COLUMN_IDEAL_WEIGHT, idealWeight)
            put(COLUMN_BODY_MASS_INDEX, bodyMassIndex)
        }
        val result = db.insert(TABLE_LOG, null, values)
        db.close()
        return result != -1L
    }

    // Retrieve logs for a specific account.
    fun getLogsByAccountId(accountId: Int): List<Map<String, Any?>> {
        val logs = mutableListOf<Map<String, Any?>>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_LOG,
            null,
            "$COLUMN_ACCOUNT_ID = ?",
            arrayOf(accountId.toString()),
            null,
            null,
            "$COLUMN_TIMESTAMP DESC"
        )
        if (cursor.moveToFirst()) {
            do {
                val log = mapOf(
                    COLUMN_LOG_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_LOG_ID)),
                    COLUMN_SLEEP_SCHEDULE to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SLEEP_SCHEDULE)),
                    COLUMN_FAT_PERCENTAGE to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_FAT_PERCENTAGE)),
                    COLUMN_IDEAL_WEIGHT to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_IDEAL_WEIGHT)),
                    COLUMN_BODY_MASS_INDEX to cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_BODY_MASS_INDEX)),
                    COLUMN_TIMESTAMP to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))
                )
                logs.add(log)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return logs
    }

    // Check if an account exists with the given email and password.
    fun checkAccount(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_ACCOUNT,
            arrayOf(COLUMN_ID),
            "$COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(email, password),
            null,
            null,
            null
        )
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // Get user info by email
    fun getUserByEmail(email: String): Map<String, Any>? {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_ACCOUNT,
            arrayOf(COLUMN_ID, COLUMN_FIRST_NAME, COLUMN_LAST_NAME),
            "$COLUMN_EMAIL = ?",
            arrayOf(email),
            null,
            null,
            null
        )
        var userInfo: Map<String, Any>? = null
        if (cursor.moveToFirst()) {
            userInfo = mapOf(
                COLUMN_ID to cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                COLUMN_FIRST_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FIRST_NAME)),
                COLUMN_LAST_NAME to cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_LAST_NAME))
            )
        }
        cursor.close()
        db.close()
        return userInfo
    }
}
