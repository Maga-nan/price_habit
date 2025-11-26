package com.gk.priceofhabit.ui.settings

import android.content.Context
import android.content.SharedPreferences

class SettingsStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun setCurrency(currency: String) {
        prefs.edit().putString("currency", currency).apply()
    }

    fun getCurrency(): String =
        prefs.getString("currency", "Br") ?: "Br"

    fun setLanguage(lang: String) {
        prefs.edit().putString("language", lang).apply()
    }

    fun getLanguage(): String =
        prefs.getString("language", "ru") ?: "ru"
}
