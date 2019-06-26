package com.mycroft.roomdemo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wordlist")
data class Word(
    @PrimaryKey(autoGenerate = true)
    var wordId: Int? = null,
    var wordfrom: String,
    var wordto: String,
    var favflag: String
)