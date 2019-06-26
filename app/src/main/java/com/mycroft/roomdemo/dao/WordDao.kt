package com.mycroft.roomdemo.dao

import androidx.room.Dao
import androidx.room.Query
import com.mycroft.roomdemo.entity.Word

@Dao
interface WordDao {

    @Query("SELECT * FROM wordlist")
    fun queryWord(): List<Word>
}