package com.example.myprojetsynthese2

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myprojetsynthese2.fragment.FragmentIndiceDeMasseCorporelle
import com.example.myprojetsynthese2.fragment.FragmentPoidsIdeal
import com.example.myprojetsynthese2.fragment.FragmentPoucentageDeMatieresGrasseas
import com.example.myprojetsynthese2.fragment.FragmentSommeilSain
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Déclaration des vues sans initialisation immédiate
    lateinit var btnSommeil: Button
    lateinit var btnPoucentageMatieresGrasses: Button
    lateinit var btnPoidsIdeal: Button
    lateinit var btnPointCorporelle: Button
    lateinit var fragment: FrameLayout
    lateinit var sLangue: Spinner

    // Liste des langues
    val listOfLanguage = listOf<String>("العربية", "English")

    // recuper la langue selecte
    val longueSelecte = intent?.getStringExtra("langue") ?: "en"

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Get user info from intent extras
        currentAccountId = intent.getIntExtra("accountId", -1)
        currentFirstName = intent.getStringExtra("firstName") ?: ""
        currentLastName = intent.getStringExtra("lastName") ?: ""

        // Initialisation des vues après setContentView
        btnSommeil = findViewById(R.id.btnSommeil)
        btnPoucentageMatieresGrasses = findViewById(R.id.btnPoucentageMatieresGrasses)
        btnPoidsIdeal = findViewById(R.id.btnPoidsIdeal)
        btnPointCorporelle = findViewById(R.id.btnPointCorporelle)
        fragment = findViewById(R.id.fl)
        sLangue = findViewById(R.id.sLangue)

        val ivHistory = findViewById<android.widget.ImageView>(R.id.ivHistory)
        ivHistory.setOnClickListener {
            val intent = android.content.Intent(this, LogActivity::class.java)
            val accountId = getLoggedInAccountId()
            val firstName = getLoggedInFirstName()
            val lastName = getLoggedInLastName()
            intent.putExtra("accountId", accountId)
            intent.putExtra("firstName", firstName)
            intent.putExtra("lastName", lastName)
            startActivity(intent)
        }

    }

    // Simulated current logged-in user info
    private var currentAccountId: Int = -1
    private var currentFirstName: String = ""
    private var currentLastName: String = ""

    // Placeholder methods to get logged-in user info
    fun getLoggedInAccountId(): Int {
        return currentAccountId
    }

    fun getLoggedInFirstName(): String {
        return currentFirstName
    }

    fun getLoggedInLastName(): String {
        return currentLastName
    }

    // Method to simulate user login (for testing)
    fun loginUser(accountId: Int, firstName: String, lastName: String) {
        currentAccountId = accountId
        currentFirstName = firstName
        currentLastName = lastName
    }

    @SuppressLint("CommitTransaction")
    override fun onStart() {
        super.onStart()

        // Apply window insets listener here
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Spinner des langues
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, listOfLanguage)
        sLangue.adapter = adapter

        // Afficher fragment sommeil sain par défaut
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl, FragmentSommeilSain())
                .commit()
        }

        // Appel à la fonction pour changer la couleur du bouton sélectionné
        changerCouleurBoutonSelectionne(btnSommeil)

        // btnSommeil
        btnSommeil.setOnClickListener {
            val fragment = FragmentSommeilSain()
            val bundle = android.os.Bundle()
            bundle.putString("clickedButton", "btnSommeil")
            bundle.putInt("accountId", currentAccountId)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl, fragment)
                    .commit()
            }
            changerCouleurBoutonSelectionne(btnSommeil)
        }

        // btnPoucentageMatieresGrasses
        btnPoucentageMatieresGrasses.setOnClickListener {
            val fragment = FragmentPoucentageDeMatieresGrasseas()
            val bundle = android.os.Bundle()
            bundle.putString("clickedButton", "btnPoucentageMatieresGrasses")
            bundle.putInt("accountId", currentAccountId)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl, fragment)
                    .commit()
            }
            changerCouleurBoutonSelectionne(btnPoucentageMatieresGrasses)
        }

        // btnPoidsIdeal
        btnPoidsIdeal.setOnClickListener {
            val fragment = FragmentPoidsIdeal()
            val bundle = android.os.Bundle()
            bundle.putString("clickedButton", "btnPoidsIdeal")
            bundle.putInt("accountId", currentAccountId)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl, fragment)
                    .commit()
            }
            changerCouleurBoutonSelectionne(btnPoidsIdeal)
        }

        // btnPointCorporelle
        btnPointCorporelle.setOnClickListener {
            val fragment = FragmentIndiceDeMasseCorporelle()
            val bundle = android.os.Bundle()
            bundle.putString("clickedButton", "btnPointCorporelle")
            bundle.putInt("accountId", currentAccountId)
            fragment.arguments = bundle
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.fl, fragment)
                    .commit()
            }
            changerCouleurBoutonSelectionne(btnPointCorporelle)
        }

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

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    })

     fun attachBaseContext(newBase: android.content.Context) {
        val locale = Locale.getDefault()
        val config = newBase.resources.configuration
        config.setLocale(locale)
        val context = newBase.createConfigurationContext(config)
        super.attachBaseContext(context)
    }
    }

    // Fonction pour changer dynamiquement la couleur du bouton sélectionné
    fun changerCouleurBoutonSelectionne(boutonClique: Button) {
        val boutons = listOf(btnSommeil, btnPoucentageMatieresGrasses, btnPoidsIdeal, btnPointCorporelle)

        for (bouton in boutons) {
            if (bouton == boutonClique) {
                bouton.setBackgroundColor(Color.WHITE)
                bouton.setTextColor(Color.BLACK)
            } else {
                bouton.setBackgroundColor(Color.parseColor("#136D64"))
                bouton.setTextColor(Color.WHITE)
            }
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
