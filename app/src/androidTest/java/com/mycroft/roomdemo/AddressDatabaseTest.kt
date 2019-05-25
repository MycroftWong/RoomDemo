package com.mycroft.roomdemo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.blankj.utilcode.util.GsonUtils
import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mycroft.roomdemo.dao.AddressDao
import com.mycroft.roomdemo.dao.AddressDatabase
import com.mycroft.roomdemo.entity.City
import com.mycroft.roomdemo.entity.County
import com.mycroft.roomdemo.entity.Province
import com.mycroft.roomdemo.entity.Street
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

@RunWith(AndroidJUnit4::class)
class AddressDatabaseTest {

    private lateinit var dao: AddressDao

    private lateinit var database: AddressDatabase

    private lateinit var context: Context

    @Before
    fun createDb() {

        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AddressDatabase::class.java)
            .build()
        dao = database.addressDao()

        insertAll(dao)
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        database.close()
    }

    @Test
    @Throws(Exception::class)
    fun testStreetDetailQuery() {
        val street = dao.loadStreetDetail(26461)
        LogUtils.e(GsonUtils.toJson(street))
    }

    @Test
    @Throws(Exception::class)
    fun testQuery() {
        val provinceList = dao.loadAllPrivinces()

        LogUtils.e(GsonUtils.toJson(provinceList))

        val cityList = dao.loadCitiesByProvinceId(22)
        LogUtils.e(GsonUtils.toJson(cityList))

        val countyList = dao.loadCountiesByCityId(326)
        LogUtils.e(GsonUtils.toJson(countyList))

        val streetList = dao.loadStreetsByCountyId(3441)
        LogUtils.e(GsonUtils.toJson(streetList))
    }

    /**
     * 把所有数据添加到数据库中
     **/
    private fun insertAll(dao: AddressDao) {
        val assetManager = context.assets

        val provinces = transferProvince(getJsonArray(assetManager.open("province.json")))
        dao.insertProvinces(provinces)

        val cities = transferCity(getJsonArray(assetManager.open("city.json")))
        dao.insertCities(cities)

        val counties = transferCounty(getJsonArray(assetManager.open("county.json")))
        dao.insertCounties(counties)

        val streets = transferStreet(getJsonArray(assetManager.open("street.json")))
        dao.insertStreets(streets)
    }


    companion object {
        private fun readFile(input: InputStream): Reader {
            return BufferedReader(InputStreamReader(input))
        }

        private val GSON = Gson()

        private fun getJsonArray(input: InputStream): JsonArray {
            val jsonParser = JsonParser()
            return jsonParser.parse(readFile(input)).asJsonArray
        }

        private fun transferProvince(jsonArray: JsonArray): List<Province> {
            val provinces = mutableListOf<Province>()

            for (item in jsonArray) {
                provinces.add(GSON.fromJson(item, Province::class.java))
            }

            return provinces
        }

        private fun transferCity(jsonArray: JsonArray): List<City> {
            val cities = mutableListOf<City>()

            for (item in jsonArray) {
                cities.add(GSON.fromJson(item, City::class.java))
            }

            return cities
        }

        private fun transferCounty(jsonArray: JsonArray): List<County> {
            val counties = mutableListOf<County>()

            for (item in jsonArray) {
                counties.add(GSON.fromJson(item, County::class.java))
            }

            return counties
        }

        private fun transferStreet(jsonArray: JsonArray): List<Street> {
            val streets = mutableListOf<Street>()

            for (item in jsonArray) {
                streets.add(GSON.fromJson(item, Street::class.java))
            }

            return streets
        }
    }

}