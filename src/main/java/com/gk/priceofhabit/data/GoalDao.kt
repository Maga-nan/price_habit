package com.gk.priceofhabit.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gk.priceofhabit.entity.Goal     // ← ЭТО ОБЯЗАТЕЛЬНО!
import kotlinx.coroutines.flow.Flow
import androidx.room.Delete

@Dao
interface GoalDao {

    @Insert
    suspend fun insert(goal: Goal)

    @Query("SELECT * FROM Goal ORDER BY id DESC")
    fun getAll(): Flow<List<Goal>>
    @Delete
    suspend fun delete(goal: Goal)

}

