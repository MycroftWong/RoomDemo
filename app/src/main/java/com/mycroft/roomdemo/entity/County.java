package com.mycroft.roomdemo.entity;


import androidx.room.*;
import com.google.gson.annotations.SerializedName;

@Entity(tableName = "county",
        foreignKeys = @ForeignKey(entity = City.class, parentColumns = "id", childColumns = "city_id"),
        indices = @Index("city_id"))
public class County {
    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;
    @SerializedName("city_id")
    @ColumnInfo(name = "city_id")
    public int cityId;
    @ColumnInfo(name = "name")
    public String name;

    public County() {
    }

    @Ignore
    public County(int id, int cityId, String name) {
        this.id = id;
        this.cityId = cityId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}