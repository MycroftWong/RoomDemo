package com.mycroft.roomdemo.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.blankj.utilcode.util.LogUtils;
import com.mycroft.roomdemo.entity.Word;

@Database(entities = {Word.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WordDao getWordDao();

    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            LogUtils.e(database);

            database.beginTransaction();
/*
            Cursor cursor = database.query("SELECT * FROM wordlist");

            if (!cursor.moveToFirst()) {
                LogUtils.e("nothing");
                return;
            }

            while (cursor.moveToNext()) {
                int wordId = cursor.getInt(cursor.getColumnIndex("wordId"));
                LogUtils.e(wordId);
            }*/

            database.endTransaction();
        }
    };
}
