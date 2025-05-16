package com.example.myprojetsynthese2.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.myprojetsynthese2.R
import kotlin.math.log10


class FragmentPoucentageDeMatieresGrasseas : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_poucentage_de_matieres_grasseas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Récupération des vues
        val lyPourcentage_2 = view.findViewById<LinearLayout>(R.id.lyPourcentage_2)
        val etAge_fragment = view.findViewById<EditText>(R.id.etAge_fragment)
        val etTaille_fragment = view.findViewById<EditText>(R.id.etTaille_fragment)
        val etPoids_fragment = view.findViewById<EditText>(R.id.etPoids_fragment)
        val etCou_fragment = view.findViewById<EditText>(R.id.etCou_fragment)
        val etCeinture_fragment = view.findViewById<EditText>(R.id.etTailleCeinture_fragment)
        val etHanche_fragment = view.findViewById<EditText>(R.id.etHanche_fragment)
        val rgGender_fragment = view.findViewById<RadioGroup>(R.id.rgGender_fragment)
        val rbHomme_fragment = view.findViewById<RadioButton>(R.id.rbMale_fragment)
        val rbFemme_fragment = view.findViewById<RadioButton>(R.id.rbFemale_fragment)
        val btnCalculer_fragment = view.findViewById<Button>(R.id.btnCalculerGraisse_fragment)
        val pourcentageText_fragment = view.findViewById<TextView>(R.id.percentageText)
        val typeText_fragment = view.findViewById<TextView>(R.id.typeText)
        val progressBar_fragment = view.findViewById<ProgressBar>(R.id.pv_fragment)
        val tvHanche_fragment: TextView = view.findViewById(R.id.tvHanche_fragment)

        // Vérification du sexe au changement
        rgGender_fragment.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbMale_fragment) {
                // Cacher le champ de hanche si sexe est homme
                etHanche_fragment.visibility = View.GONE
                tvHanche_fragment.visibility = View.GONE
            } else if (checkedId == R.id.rbFemale_fragment) {
                // Afficher le champ de hanche si sexe est femme
                etHanche_fragment.visibility = View.VISIBLE
                tvHanche_fragment.visibility = View.VISIBLE
            }
        }

        btnCalculer_fragment.setOnClickListener {
            // Vérifier si les les champs sont vide
            if (etAge_fragment.text.isEmpty() || etAge_fragment.text.isBlank()) {
                etAge_fragment.error = getString(R.string.entrez_age_valide)
                return@setOnClickListener
            }

            if (etPoids_fragment.text.isEmpty() || etPoids_fragment.text.isBlank()) {
                etPoids_fragment.error = getString(R.string.error_enter_weight)
                return@setOnClickListener
            }

            if (etTaille_fragment.text.isEmpty() || etTaille_fragment.text.isBlank()) {
                etTaille_fragment.error = getString(R.string.erreur_taille_requise)
                return@setOnClickListener
            }

            if (etCou_fragment.text.isEmpty() || etCou_fragment.text.isBlank()) {
                etCou_fragment.error = getString(R.string.erreur_cou_requis)
                return@setOnClickListener
            }

            if (etCeinture_fragment.text.isEmpty() || etCeinture_fragment.text.isBlank()) {
                etCeinture_fragment.error = getString(R.string.erreur_ceinture_requise)
                return@setOnClickListener
            }

            if ((rbFemme_fragment.isChecked) && (etHanche_fragment.text.isEmpty() || etHanche_fragment.text.isBlank())) {
                etHanche_fragment.error = getString(R.string.erreur_hanche_requise)
                return@setOnClickListener
            }

            try {
                val age = etAge_fragment.text.toString().toInt()
                val poids = etPoids_fragment.text.toString().toDouble()
                val taille = etTaille_fragment.text.toString().toDouble()
                val cou = etCou_fragment.text.toString().toDouble()
                val ceinture = etCeinture_fragment.text.toString().toDouble()

                // Masquer/afficher le champ des hanches en fonction du sexe sélectionné.
                val hanche: Double = if (rgGender_fragment.checkedRadioButtonId == R.id.rbFemale_fragment) {
                    etHanche_fragment.text.toString().toDoubleOrNull() ?: 90.0 // la valeur par defaut les femmes
                } else {
                    90.0 // la valeur par defaut pour les hommes
                }

                val pourcentageGraisse = when (rgGender_fragment.checkedRadioButtonId) {
                    R.id.rbMale_fragment -> {
                        495 / (1.0324 - 0.19077 * log10(ceinture - cou) + 0.15456 * log10(taille)) - 450
                    }
                    R.id.rbFemale_fragment -> {
                        495 / (1.29579 - 0.35004 * log10(ceinture + hanche - cou) + 0.221 * log10(taille)) - 450
                    }
                    else -> {
                        return@setOnClickListener
                    }
                }

                val graisseFinale = pourcentageGraisse.coerceIn(0.0, 40.0)
                val arrondi = String.format("%.1f", graisseFinale)
                progressBar_fragment.progress = graisseFinale.toInt()
                pourcentageText_fragment.text = "$arrondi%"

                typeText_fragment.text = when {
                    graisseFinale < 6 ->  getString(R.string.fat_level_essential)
                    graisseFinale in 6.0..13.0 -> getString(R.string.fat_level_athlete)
                    graisseFinale in 14.0..17.0 -> getString(R.string.fat_level_fitness)
                    graisseFinale in 18.0..24.0 -> getString(R.string.fat_level_average)
                    else -> getString(R.string.fat_level_high)
                }

            } catch (e: Exception) {
                //Toast.makeText(requireContext(), getString(R.string.error_fill_fields), Toast.LENGTH_SHORT).show()
            }
            
            // 
            lyPourcentage_2.visibility = view.visibility

            // Log the fat percentage input
            val dbHelper = com.example.myprojetsynthese2.DatabaseHelper(requireContext())
            val accountId = arguments?.getInt("accountId") ?: -1
            if (accountId != -1) {
                val fatPercentage = pourcentageText_fragment.text.toString().removeSuffix("%").toDoubleOrNull()
                dbHelper.insertLog(accountId, null, fatPercentage, null, null)
            }
        }

    }
}


