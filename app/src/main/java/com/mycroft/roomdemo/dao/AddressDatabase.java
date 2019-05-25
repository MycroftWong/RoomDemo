package com.mycroft.roomdemo.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mycroft.roomdemo.entity.*;

/**
 * @author mycroft
 */
@Database(entities = {Province.class, City.class, County.class, Street.class},
        views = {StreetDetailInfo.class},
        version = 1,
        exportSchema = false)
public abstract class AddressDatabase extends RoomDatabase {

    public abstract AddressDao addressDao();

}
