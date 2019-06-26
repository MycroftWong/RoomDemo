package com.mycroft.roomdemo;

import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.LogUtils;
import com.mycroft.roomdemo.dao.AddressDao;
import com.mycroft.roomdemo.dao.AddressDatabase;
import com.mycroft.roomdemo.entity.Province;
import com.mycroft.roomdemo.util.AddressDatabaseCopier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.mycroft.roomdemo.dao.AddressDatabase.MIGRATION_1_2;

@RunWith(AndroidJUnit4.class)
public class AddressDataCopyTest {

    private Context context;
    private AddressDatabase addressDatabase;
    private AddressDao addressDao;

    @Before
    public void createDB() {
        context = ApplicationProvider.getApplicationContext();

        addressDatabase = AddressDatabaseCopier.getInstance(context).getRoomDatabase();
        addressDao = addressDatabase.addressDao();
    }

    @After
    public void closeDB() {
        addressDatabase.close();
    }

    @Test
    public void testQuery() {
        List<Province> provinces = addressDao.loadAllProvinces();
        LogUtils.e(GsonUtils.toJson(provinces));
    }
}
