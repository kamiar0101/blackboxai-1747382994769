package com.example.myprojetsynthese2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.myprojetsynthese2.R

class FragmentIndiceDeMasseCorporelle : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_indice_de_masse_corporelle, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val lyIndice_2: LinearLayout by lazy { view.findViewById(R.id.lyIndice_2) }
        val etTaille_fragment: EditText by lazy { view.findViewById(R.id.etTaille_fragment) }
        val etPoids_fragment: EditText by lazy { view.findViewById(R.id.etPoids_fragment) }
        val btnCalculerGraisseNasseCorporelle_fragment: Button by lazy { view.findViewById(R.id.btnCalculerGraisseNasseCorporelle_fragment) }
        val tvGraisseNasseCorporelle_fragment: TextView by lazy { view.findViewById(R.id.tvGraisseNasseCorporelle_fragment) }
        val tvPointNormal_fragment: TextView by lazy { view.findViewById(R.id.tvPointNormal_fragment) }
        val pv_fragment: ProgressBar by lazy { view.findViewById(R.id.pv_fragment) }

        //


        btnCalculerGraisseNasseCorporelle_fragment.setOnClickListener {
            // Vérifier si les les champs sont vide
            if (etTaille_fragment.text.isEmpty() || etTaille_fragment.text.isBlank()){
                etTaille_fragment.error = getString(R.string.erreur_taille_requise)
                return@setOnClickListener
            }
            if (etPoids_fragment.text.isEmpty() || etPoids_fragment.text.isBlank() ) {
                etPoids_fragment.error = getString(R.string.error_enter_weight)
                return@setOnClickListener
            }

            val tailleStr = etTaille_fragment.text.toString()
            val poidsStr = etPoids_fragment.text.toString()

            try {
                val longueur = tailleStr.toDouble() / 100 // Convert cm to m
                val poids = poidsStr.toDouble()

                if (longueur <= 0 ) {
                    etTaille_fragment.error = getString(R.string.erreur_taille_requise)
                    return@setOnClickListener
                }
                if (poids <= 0) {
                    etPoids_fragment.error = getString(R.string.erreur_taille_requise)
                    return@setOnClickListener
                }

                val bmi = poids / (longueur * longueur)
                tvGraisseNasseCorporelle_fragment.text = "% " + String.format("%.2f", bmi)

                //
                pv_fragment.progress = bmi.toInt()

                //
                when {
                    bmi < 18.5 -> tvPointNormal_fragment.text = getString(R.string.underweight)

                    bmi in 18.5..24.9 -> tvPointNormal_fragment.text = getString(R.string.normal_weight)

                    bmi in 25.0..29.9 -> tvPointNormal_fragment.text = getString(R.string.overweight)
                    else -> tvPointNormal_fragment.text = getString(R.string.obesity)
                }

                // Log the BMI input
                val dbHelper = com.example.myprojetsynthese2.DatabaseHelper(requireContext())
                val accountId = arguments?.getInt("accountId") ?: -1
                if (accountId != -1) {
                    dbHelper.insertLog(accountId, null, null, null, bmi)
                }

            } catch (e: NumberFormatException) {
                //Toast.makeText(requireContext(), "إدخال غير صالح", Toast.LENGTH_SHORT).show()
            }

            //
            lyIndice_2.visibility = view.visibility
        }
    }

}