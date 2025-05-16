package com.example.myprojetsynthese2

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class PageLogin : AppCompatActivity() {
    // Déclaration des vues paresseuse
    lateinit var sLangue: Spinner
    lateinit var btnLogin: Button
    lateinit var tvSignUp: TextView

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText

    // Liste des langues
    val listOfLanguage = listOf<String>("العربية", "English")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_page_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main1)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialisation des vues après setContentView
        sLangue = findViewById(R.id.sLangue)
        btnLogin = findViewById(R.id.btnLogin)
        tvSignUp = findViewById(R.id.tvSignUp)

        etEmail = findViewById(R.id.etEmail_fragment)
        etPassword = findViewById(R.id.etPassword_fragment)

        // Spinner des langues
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listOfLanguage)
        sLangue.adapter = adapter

        // Initialiser le Spinner avec la langue actuelle
        sLangue.setSelection(if (intent.getStringExtra("selectedLangCode") == "ar") 0 else 1)

        // Listener du Spinner
        sLangue.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLangCode = if (position == 0) "ar" else "en"
                // Vérifier si la langue sélectionnée est différente de la langue actuelle
                if (Locale.getDefault().language != selectedLangCode) {
                    changerLangue(selectedLangCode)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ne rien faire
            }
        })

        // Aller a l'activite principale MainActivity
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString()

            if (email.isEmpty()) {
                etEmail.error = getString(R.string.adresse_email) + " " + getString(R.string.required)
                etEmail.requestFocus()
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                etPassword.error = getString(R.string.mot_passe) + " " + getString(R.string.required)
                etPassword.requestFocus()
                return@setOnClickListener
            }

            val dbHelper = DatabaseHelper(this)
            val isValidUser = dbHelper.checkAccount(email, password)
            if (isValidUser) {
                Toast.makeText(this, getString(R.string.connexion_reussie), Toast.LENGTH_LONG).show()
                val selectedLangCode = if (sLangue.selectedItemPosition == 0) "ar" else "en"
                val userInfo = dbHelper.getUserByEmail(email)
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("selectedLangCode", selectedLangCode)
                if (userInfo != null) {
                    intent.putExtra("accountId", userInfo[DatabaseHelper.COLUMN_ID] as Int)
                    intent.putExtra("firstName", userInfo[DatabaseHelper.COLUMN_FIRST_NAME] as String)
                    intent.putExtra("lastName", userInfo[DatabaseHelper.COLUMN_LAST_NAME] as String)
                }
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.email_ou_mot_passe_incorrect), Toast.LENGTH_LONG).show()
            }
        }

        // Aller a l'activite PageInscription
        tvSignUp.setOnClickListener{
            val intent = Intent(this, PageInscription::class.java)
            startActivity(intent)
            //
            finish()
        }

    }


    // Fonction pour changer la langue
    fun changerLangue(codeLangue: String) {
        val locale = Locale(codeLangue)
        Locale.setDefault(locale)

        val config = Configuration()


        val localeWithLatinNumbers = Locale.Builder()
            .setLocale(locale)
            //.setUnicodeLocaleKeyword("nu", "latn")
            .build()

        config.setLocale(localeWithLatinNumbers)
        config.setLayoutDirection(localeWithLatinNumbers)

        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        recreate()
    }
}
