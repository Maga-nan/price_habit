package com.gk.priceofhabit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.gk.priceofhabit.entity.Income
import kotlinx.coroutines.flow.Flow

@Dao
interface IncomeDao {

    @Insert
    suspend fun insert(income: Income)

    @Query("SELECT * FROM income ORDER BY id DESC")
    fun getAll(): Flow<List<Income>>

    @Delete
    suspend fun delete(income: Income)

}

