package com.example.myprojetsynthese2

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*

class LogActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var accountId: Int = -1
    private var firstName: String = ""
    private var lastName: String = ""

    private lateinit var tvUserName: TextView
    private lateinit var lvLogs: ListView
    private lateinit var btnDownload: Button
    private lateinit var btnShare: Button
    private lateinit var btnDelete: Button
    private lateinit var btnBack: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        dbHelper = DatabaseHelper(this)

        tvUserName = findViewById(R.id.tvUserName)
        lvLogs = findViewById(R.id.lvLogs)
        btnDownload = findViewById(R.id.btnDownload)
        btnShare = findViewById(R.id.btnShare)
        btnDelete = findViewById(R.id.btnDelete)
        btnBack = findViewById(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        accountId = intent.getIntExtra("accountId", -1)
        firstName = intent.getStringExtra("firstName") ?: "User"
        lastName = intent.getStringExtra("lastName") ?: ""

        tvUserName.text = "$firstName $lastName"

        loadLogs()

        btnDownload.setOnClickListener {
            downloadLogs()
        }

        btnShare.setOnClickListener {
            shareLogs()
        }

        btnDelete.setOnClickListener {
            deleteLogs()
        }
    }

    override fun onResume() {
        super.onResume()
        loadLogs()
    }

    private fun loadLogs() {
        try {
            if (accountId == -1) {
                Toast.makeText(this, "Invalid account", Toast.LENGTH_SHORT).show()
                return
            }
            val logs = dbHelper.getLogsByAccountId(accountId)
            val logStrings = logs.map { logEntry ->
                buildString {
                    logEntry[DatabaseHelper.COLUMN_SLEEP_SCHEDULE]?.let {
                        append("Sleep Schedule: $it\n")
                    }
                    logEntry[DatabaseHelper.COLUMN_FAT_PERCENTAGE]?.let {
                        append("Fat Percentage: $it\n")
                    }
                    logEntry[DatabaseHelper.COLUMN_IDEAL_WEIGHT]?.let {
                        append("Ideal Weight: $it\n")
                    }
                    logEntry[DatabaseHelper.COLUMN_BODY_MASS_INDEX]?.let {
                        append("BMI: $it\n")
                    }
                    append("Timestamp: ${logEntry[DatabaseHelper.COLUMN_TIMESTAMP]}")
                }
            }
            val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, logStrings)
            lvLogs.adapter = adapter
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading logs: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun downloadLogs() {
        val logs = dbHelper.getLogsByAccountId(accountId)
        val logText = logs.joinToString(separator = "\n\n") { logEntry ->
            "Sleep Schedule: ${logEntry[DatabaseHelper.COLUMN_SLEEP_SCHEDULE]}\n" +
            "Fat Percentage: ${logEntry[DatabaseHelper.COLUMN_FAT_PERCENTAGE]}\n" +
            "Ideal Weight: ${logEntry[DatabaseHelper.COLUMN_IDEAL_WEIGHT]}\n" +
            "BMI: ${logEntry[DatabaseHelper.COLUMN_BODY_MASS_INDEX]}\n" +
            "Timestamp: ${logEntry[DatabaseHelper.COLUMN_TIMESTAMP]}"
        }
        try {
            val file = File(cacheDir, "logs_${accountId}.txt")
            FileOutputStream(file).use { it.write(logText.toByteArray()) }
            Toast.makeText(this, "Logs saved to ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving logs: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun shareLogs() {
        val logs = dbHelper.getLogsByAccountId(accountId)
        val logText = logs.joinToString(separator = "\n\n") { logEntry ->
            "Sleep Schedule: ${logEntry[DatabaseHelper.COLUMN_SLEEP_SCHEDULE]}\n" +
            "Fat Percentage: ${logEntry[DatabaseHelper.COLUMN_FAT_PERCENTAGE]}\n" +
            "Ideal Weight: ${logEntry[DatabaseHelper.COLUMN_IDEAL_WEIGHT]}\n" +
            "BMI: ${logEntry[DatabaseHelper.COLUMN_BODY_MASS_INDEX]}\n" +
            "Timestamp: ${logEntry[DatabaseHelper.COLUMN_TIMESTAMP]}"
        }
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, logText)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share logs via"))
    }

    private fun deleteLogs() {
        if (accountId == -1) {
            Toast.makeText(this, "Invalid account", Toast.LENGTH_SHORT).show()
            return
        }
        val db = dbHelper.writableDatabase
        val deleted = db.delete(DatabaseHelper.TABLE_LOG, "${DatabaseHelper.COLUMN_ACCOUNT_ID} = ?", arrayOf(accountId.toString()))
        db.close()
        Toast.makeText(this, "Deleted $deleted logs", Toast.LENGTH_SHORT).show()
        loadLogs()
    }
}