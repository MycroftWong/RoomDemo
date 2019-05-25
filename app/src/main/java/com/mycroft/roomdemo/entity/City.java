package com.mycroft.roomdemo.entity;


import androidx.room.*;
import com.google.gson.annotations.SerializedName;

/**
 * @author mycroft
 */
@Entity(tableName = "city",
        foreignKeys = @ForeignKey(entity = Province.class, parentColumns = "id", childColumns = "province_id"),
        indices = @Index("province_id"))
public class City {

    @PrimaryKey
    @ColumnInfo(name = "id")
    public int id;

    @SerializedName("province_id")
    @ColumnInfo(name = "province_id")
    public int provinceId;

    @ColumnInfo(name = "name")
    public String name;

    public City() {
    }

    @Ignore
    public City(int id, int provinceId, String name) {
        this.id = id;
        this.provinceId = provinceId;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}