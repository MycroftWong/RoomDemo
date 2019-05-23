package com.mycroft.roomdemo.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.mycroft.roomdemo.entity.City;
import com.mycroft.roomdemo.entity.County;
import com.mycroft.roomdemo.entity.Province;
import com.mycroft.roomdemo.entity.Street;

/**
 * @author mycroft
 */
@Database(entities = {Province.class, City.class, County.class, Street.class}, version = 1)
public abstract class AddressDatabase extends RoomDatabase {

    public abstract AddressDao addressDao();
}
