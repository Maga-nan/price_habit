package com.gk.priceofhabit.data

import android.content.Context           // ← Добавлено
import androidx.room.Database
import androidx.room.Room               // ← Добавлено
import androidx.room.RoomDatabase
import com.gk.priceofhabit.entity.Expense
import com.gk.priceofhabit.entity.Income
import com.gk.priceofhabit.entity.Goal

@Database(
    entities = [Expense::class, Income::class, Goal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun incomeDao(): IncomeDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "price_of_habit.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
