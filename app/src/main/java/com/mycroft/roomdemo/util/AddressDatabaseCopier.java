package com.mycroft.roomdemo.util;

import android.content.Context;
import android.util.Log;
import androidx.room.Room;
import com.mycroft.roomdemo.dao.AddressDatabase;

import java.io.*;

public class AddressDatabaseCopier {

    private static final String TAG = DatabaseCopier.class.getSimpleName();
    private static final String DATABASE_NAME = "address.db";

    private AddressDatabase addressDatabase;
    private static Context appContext;

    private static class Holder {
        private static final AddressDatabaseCopier INSTANCE = new AddressDatabaseCopier();
    }

    public static AddressDatabaseCopier getInstance(Context context) {
        appContext = context;
        return Holder.INSTANCE;
    }

    private AddressDatabaseCopier() {
        //call method that check if database not exists and copy prepopulated file from assets
        copyAttachedDatabase(appContext, DATABASE_NAME);

        addressDatabase = Room.databaseBuilder(appContext,
                AddressDatabase.class, DATABASE_NAME)
                .addMigrations(AddressDatabase.MIGRATION_1_2)
                .build();
    }

    public AddressDatabase getRoomDatabase() {
        return addressDatabase;
    }


    private void copyAttachedDatabase(Context context, String databaseName) {
        final File dbPath = context.getDatabasePath(databaseName);

        // If the database already exists, return
        if (dbPath.exists()) {
            return;
        }

        // Make sure we have a path to the file
        dbPath.getParentFile().mkdirs();

        // Try to copy database file
        try {
            final InputStream inputStream = context.getAssets().open("address.db");
            final OutputStream output = new FileOutputStream(dbPath);

            byte[] buffer = new byte[8192];
            int length;

            while ((length = inputStream.read(buffer, 0, 8192)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            inputStream.close();
        } catch (IOException e) {
            Log.d(TAG, "Failed to open file", e);
            e.printStackTrace();
        }
    }

}
