package com.example.myprojetsynthese2.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.myprojetsynthese2.R

class FragmentPoidsIdeal : Fragment() {

    private lateinit var genderGroup: RadioGroup
    private lateinit var maleRadioButton: RadioButton
    private lateinit var femaleRadioButton: RadioButton
    private lateinit var heightEditText: EditText
    private lateinit var calculateButton: Button

    private lateinit var clPoids_2: ConstraintLayout
    private lateinit var percentageText: TextView
    private lateinit var tvDevine: TextView
    private lateinit var tvRobinsone: TextView
    private lateinit var tvMiller: TextView
    private lateinit var tvHamwi: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_poids_ideal, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialisation des vues
        genderGroup = view.findViewById(R.id.rgGender_fragment)
        maleRadioButton = view.findViewById(R.id.rbMale_fragment)
        femaleRadioButton = view.findViewById(R.id.rbFemale_fragment)
        heightEditText = view.findViewById(R.id.etAge_fragment)
        calculateButton = view.findViewById(R.id.btnCalculerGraisse_fragment)

        clPoids_2 = view.findViewById(R.id.clPoids_2)
        percentageText = view.findViewById(R.id.percentageText)
        tvDevine = view.findViewById(R.id.tvDevine)
        tvRobinsone = view.findViewById(R.id.tvRobinsone)
        tvMiller = view.findViewById(R.id.tvMiller)
        tvHamwi = view.findViewById(R.id.tvHamwi)

        fun calculerPoidsIdeal() {
            val heightStr = heightEditText.text.toString()
            if (heightStr.isEmpty()) {
                heightEditText.error = getString(R.string.erreur_taille_requise)
                Toast.makeText(requireContext(), getString(R.string.erreur_taille_requise), Toast.LENGTH_SHORT).show()
                return
            }

            val height = heightStr.toDouble()
            val isMale = maleRadioButton.isChecked

            // Calculs
            val devine = if (isMale) 50 + 2.3 * ((height - 152.4) / 2.54) else 45.5 + 2.3 * ((height - 152.4) / 2.54)
            val robinson = if (isMale) 52 + 1.9 * ((height - 152.4) / 2.54) else 49 + 1.7 * ((height - 152.4) / 2.54)
            val miller = if (isMale) 56.2 + 1.41 * ((height - 152.4) / 2.54) else 53.1 + 1.36 * ((height - 152.4) / 2.54)
            val hamwi = if (isMale) 48 + 2.7 * ((height - 152.4) / 2.54) else 45.5 + 2.2 * ((height - 152.4) / 2.54)

            val moyenne = (devine + robinson + miller + hamwi) / 4

            // Affichage
            percentageText.text = String.format("%.1f", moyenne)
            tvDevine.text = String.format("%.1f كغ", devine)
            tvRobinsone.text = String.format("%.1f كغ", robinson)
            tvMiller.text = String.format("%.1f كغ", miller)
            tvHamwi.text = String.format("%.1f كغ", hamwi)

            //
            clPoids_2.visibility = view.visibility
        }

        // gerre le clique  sur le button calculateButton
        calculateButton.setOnClickListener {
            calculerPoidsIdeal()

            // Log the ideal weight input
            val dbHelper = com.example.myprojetsynthese2.DatabaseHelper(requireContext())
            val accountId = arguments?.getInt("accountId") ?: -1
            if (accountId != -1) {
                val idealWeight = percentageText.text.toString().toDoubleOrNull()
                dbHelper.insertLog(accountId, null, null, idealWeight, null)
            }
        }

    }

}

