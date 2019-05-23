package com.mycroft.roomdemo.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mycroft.roomdemo.entity.City;
import com.mycroft.roomdemo.entity.County;
import com.mycroft.roomdemo.entity.Province;
import com.mycroft.roomdemo.entity.Street;

import java.util.List;

@Dao
public interface AddressDao {

    /**
     * @return
     */
    @Query("SELECT * FROM province")
    List<Province> loadAllPrivinces();

    @Query("SELECT * FROM city")
    List<City> loadAllCities();

    @Query("SELECT * FROM city where province_id=:id")
    List<City> loadCitiesByProvinceId(int id);

    @Query("SELECT * FROM county")
    List<County> loadAllCounties();


    @Query("SELECT * FROM county where city_id=:id")
    List<County> loadCountiesByCityId(int id);

    @Query("SELECT * FROM street")
    List<Street> loadAllStreets();

    @Query("SELECT * FROM street where county_Id=:id")
    List<Street> loadStreetsByCountyId(int id);

    @Query("SELECT * FROM city where id=:id")
    List<City> loadCitiesById(int id);

    @Query("SELECT * FROM county where id=:id")
    List<County> loadCountiesById(int id);

    @Query("SELECT * FROM street where id=:id")
    List<Street> loadStreetsById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertProvinces(List<Province> provinces);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCities(List<City> cities);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertCounties(List<County> counties);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    List<Long> insertStreets(List<Street> streets);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertProvince(Province province);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCity(City city);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertCounty(County county);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insertStreet(Street street);

}
