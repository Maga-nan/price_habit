package com.gk.priceofhabit.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeManager(private val context: Context) {

    private val pref = context.getSharedPreferences("theme", Context.MODE_PRIVATE)
    private val _themeFlow = MutableStateFlow(loadTheme())
    val themeFlow: StateFlow<String> = _themeFlow

    private fun loadTheme(): String {
        return pref.getString("theme_mode", "system") ?: "system"
    }

    // Применить тему — этот метод вызываем из Activity
    fun apply(theme: String) {
        when (theme) {
            "light" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            "dark" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    // Сохранить тему
    suspend fun setTheme(mode: String) {
        pref.edit().putString("theme_mode", mode).apply()
        _themeFlow.emit(mode)
    }
}
