package com.mycroft.roomdemo.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * @author mycroft
 */
@Entity(tableName = "province")
public class Province {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;
    @ColumnInfo(name = "name")
    public String name;

    public Province() {
    }

    @Ignore
    public Province(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}