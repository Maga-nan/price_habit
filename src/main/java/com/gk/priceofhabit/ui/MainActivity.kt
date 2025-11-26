package com.gk.priceofhabit.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.gk.priceofhabit.R
import com.gk.priceofhabit.ui.settings.LocaleHelper
import com.gk.priceofhabit.ui.settings.SettingsStorage
import com.gk.priceofhabit.theme.ThemeManager
import com.gk.priceofhabit.ui.analytics.AnalyticsFragment
import com.gk.priceofhabit.ui.expenses.ExpensesFragment
import com.gk.priceofhabit.ui.goals.GoalsFragment
import com.gk.priceofhabit.ui.income.IncomeFragment
import com.gk.priceofhabit.ui.settings.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var fab: FloatingActionButton
    private lateinit var themeManager: ThemeManager

    override fun attachBaseContext(newBase: Context) {
        val storage = SettingsStorage(newBase)
        val newContext = LocaleHelper.applyLanguage(newBase, storage.getLanguage())
        super.attachBaseContext(newContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        themeManager = ThemeManager(this)

        // сначала применяем тему
        themeManager.apply(themeManager.themeFlow.value)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab = findViewById(R.id.fab)

        val nav = findViewById<BottomNavigationView>(R.id.bottom_nav)

        nav.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.menu_expenses -> ExpensesFragment()
                R.id.menu_income -> IncomeFragment()
                R.id.menu_goals -> GoalsFragment()
                R.id.menu_analytics -> AnalyticsFragment()
                else -> SettingsFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_container, fragment)
                .commit()

            fab.visibility =
                if (item.itemId in listOf(
                        R.id.menu_expenses,
                        R.id.menu_income,
                        R.id.menu_goals
                    )
                ) View.VISIBLE
                else View.GONE

            true
        }

        nav.selectedItemId = R.id.menu_expenses

        fab.setOnClickListener {
            val f = supportFragmentManager.findFragmentById(R.id.nav_host_container)
            if (f is AddItemHandler) f.onAddItem()
        }

        lifecycleScope.launch {
            themeManager.themeFlow.collect { newTheme ->
                themeManager.apply(newTheme)
            }
        }
    }
}
