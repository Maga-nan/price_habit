package com.gk.priceofhabit.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

import com.gk.priceofhabit.R
import com.gk.priceofhabit.theme.ThemeManager
import com.gk.priceofhabit.ui.settings.SettingsStorage
import com.gk.priceofhabit.ui.settings.LocaleHelper


class SettingsFragment : Fragment() {

    private lateinit var storage: SettingsStorage

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.fragment_settings, container, false)

        storage = SettingsStorage(requireContext())

        val langSpinner = v.findViewById<Spinner>(R.id.spinner_language)
        val curSpinner = v.findViewById<Spinner>(R.id.spinner_currency)
        val themeSpinner = v.findViewById<Spinner>(R.id.spinner_theme)

        // темы
        val themeManager = ThemeManager(requireContext())

        val themeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.theme_modes,
            android.R.layout.simple_spinner_item
        )
        themeSpinner.adapter = themeAdapter

        lifecycleScope.launch {
            themeManager.themeFlow.collect { t ->
                themeSpinner.setSelection(
                    when (t) {
                        "light" -> 0
                        "dark" -> 1
                        else -> 2
                    }
                )
            }
        }

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val mode = when (pos) {
                    0 -> "light"
                    1 -> "dark"
                    else -> "system"
                }
                lifecycleScope.launch { themeManager.setTheme(mode) }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // валюта

        val currencyValues = listOf("Br", "$", "€")
        val currencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            currencyValues
        )
        curSpinner.adapter = currencyAdapter

        curSpinner.setSelection(currencyValues.indexOf(storage.getCurrency()))

        curSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                storage.setCurrency(currencyValues[pos])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // язык

        val langs = listOf("Русский", "English")
        val langCodes = listOf("ru", "en")

        val langAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            langs
        )
        langSpinner.adapter = langAdapter

        langSpinner.setSelection(langCodes.indexOf(storage.getLanguage()))

        langSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                val lang = langCodes[pos]
                if (lang != storage.getLanguage()) {
                    storage.setLanguage(lang)
                    requireActivity().recreate()   // 🔥 вот это делает весь интерфейс новым
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        return v
    }
}
