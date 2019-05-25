package com.mycroft.roomdemo.entity

import androidx.room.DatabaseView

@DatabaseView(
    "SELECT street.id as id, street.name as name, county.name as countyName, city.name as cityName, province.name as provinceName " +
            "FROM street INNER JOIN county on street.county_id=county.id " +
            "INNER JOIN city on county.city_id=city.id " +
            "INNER JOIN province on city.province_id=province.id"
)
data class StreetDetailInfo(

    val id: Int,
    val name: String,
    val countyName: String,
    val cityName: String,
    val provinceName: String
)