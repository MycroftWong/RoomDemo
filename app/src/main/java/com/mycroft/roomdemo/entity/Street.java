package com.mycroft.roomdemo.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "street")
public class Street {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @SerializedName("county_id")
    @ColumnInfo(name = "county_Id")
    public int countyId;

    @ColumnInfo(name = "name")
    public String name;

    public Street() {
    }

    @Ignore
    public Street(int id, int countyId, String name) {
        this.id = id;
        this.countyId = countyId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}