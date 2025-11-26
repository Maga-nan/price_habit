package com.gk.priceofhabit.ui.settings

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun applyLanguage(context: Context, lang: String): Context {
        val locale = Locale(lang)
        Locale.setDefault(locale)

        val config = context.resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}

