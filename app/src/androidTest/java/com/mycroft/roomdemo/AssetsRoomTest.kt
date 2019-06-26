package com.mycroft.roomdemo

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.mycroft.roomdemo.dao.AppDatabase
import com.mycroft.roomdemo.dao.WordDao
import com.mycroft.roomdemo.util.DatabaseCopier
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AssetsRoomTest {

    private lateinit var dao: WordDao

    private lateinit var database: AppDatabase

    private lateinit var context: Context

    @Before
    fun createDB() {
        context = ApplicationProvider.getApplicationContext<Context>()

        database = DatabaseCopier.getInstance(context).roomDatabase
        dao = database.getWordDao()

    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun testAssetsRoom() {
        val word = dao.queryWord()
        LogUtils.e(GsonUtils.toJson(word))
    }
}