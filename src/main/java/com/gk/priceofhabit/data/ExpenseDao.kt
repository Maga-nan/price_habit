package com.gk.priceofhabit.data

import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.gk.priceofhabit.entity.Expense

@Dao
interface ExpenseDao {

    @Insert
    suspend fun insert(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY id DESC")
    fun getAll(): Flow<List<Expense>>
    @Delete
    suspend fun delete(expense: Expense)

}

