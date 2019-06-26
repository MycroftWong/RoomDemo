package com.mycroft.roomdemo.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.blankj.utilcode.util.LogUtils;
import com.mycroft.roomdemo.entity.*;

/**
 * @author mycroft
 */
@Database(entities = {Province.class, City.class, County.class, Street.class},
        views = {StreetDetailInfo.class},
        version = 2,
        exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {

    /**
     * 获取地址数据库DAO
     *
     * @return {@link AddressDao}
     */
    public abstract AddressDao addressDao();


    public static Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            LogUtils.e(database);

            try {
                database.beginTransaction();
                Cursor cursor = database.query("SELECT COUNT(*) FROM province");

                if (!cursor.moveToFirst()) {
                    LogUtils.e("nothing");
                } else {
                    LogUtils.e("something");
                }
            } finally {
                database.endTransaction();
            }
        }
    };
}
