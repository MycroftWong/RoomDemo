package com.mycroft.roomdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.room.Room
import chihane.jdaddressselector.AddressProvider
import chihane.jdaddressselector.OnAddressSelectedListener
import com.blankj.utilcode.util.ToastUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mycroft.roomdemo.dao.AddressDao
import com.mycroft.roomdemo.dao.AddressDatabase
import com.mycroft.roomdemo.entity.City
import com.mycroft.roomdemo.entity.County
import com.mycroft.roomdemo.entity.Province
import com.mycroft.roomdemo.entity.Street
import com.mycroft.roomdemo.view.BottomDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader

class MainActivity : AppCompatActivity() {

    val dao: AddressDao by lazy {
        val database = Room.databaseBuilder(applicationContext, AddressDatabase::class.java, "area.db")
            .build()
        val dao = database.addressDao()

        return@lazy dao
    }

    /**
     * 显示查询到的数据列表
     */
    private val data = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        insertButton.setOnClickListener { insertAll(dao) }

        queryButton.setOnClickListener { showQueryDialog() }

        addressButton.setOnClickListener { showAddressDialog() }

        recyclerView.hasFixedSize()
        recyclerView.adapter =
            object : BaseQuickAdapter<String, BaseViewHolder>(android.R.layout.simple_list_item_1, data) {
                override fun convert(helper: BaseViewHolder?, item: String?) {
                    helper?.setText(android.R.id.text1, item)
                }
            }
    }

    /**
     * 显示查询的类型dialog
     **/
    private fun showQueryDialog() {
        AlertDialog.Builder(this)
            .setAdapter(
                ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_list_item_1,
                    android.R.id.text1,
                    arrayOf("Province", "City", "County", "Street")
                )
            ) { dialog, which ->
                when (which) {
                    0 -> queryProvince()
                    1 -> queryCity()
                    2 -> queryCounty()
                    3 -> queryStreet()
                }
            }
            .show()
    }

    /**
     * 查询省份，使用 coroutines
     **/
    private fun queryProvince() {
        GlobalScope.launch {
            val provinces = dao.loadAllPrivinces()
            data.clear()
            for (item in provinces) {
                data.add("id=${item.id}, name=${item.name}")
            }

            runOnUiThread { recyclerView.adapter?.notifyDataSetChanged() }
        }
    }

    /**
     * 查询城市，使用RxJava
     **/
    private fun queryCity() {
        val disposable = dao.loadAllCities()
            .subscribeOn(Schedulers.io())
            .map {
                data.clear()
                for (item in it) {
                    data.add("id=${item.id}, name=${item.name}")
                }
                return@map data
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { recyclerView.adapter?.notifyDataSetChanged() }
    }

    /**
     * 查询区域，使用LiveData, 不确定这种用法是否正确
     **/
    private fun queryCounty() {
        dao.loadAllCounties().observe(this,
            Observer<List<County>> {
                data.clear()
                for (item in it) {
                    data.add("id=${item.id}, name=${item.name}")
                }
                recyclerView.adapter?.notifyDataSetChanged()
            })
    }

    private fun queryStreet() {
        GlobalScope.launch {
            val streets = dao.loadAllStreets()
            data.clear()
            for (item in streets) {
                data.add("id=${item.id}, name=${item.name}")
            }
            runOnUiThread { recyclerView.adapter?.notifyDataSetChanged() }
        }
    }

    /**
     * 显示地址选择dialog
     **/
    @SuppressLint("SetTextI18n")
    private fun showAddressDialog() {
        GlobalScope.launch {
            if (!isInserted()) {
                ToastUtils.showShort("please insert first!")
                return@launch
            }

            val provider = object : AddressProvider {
                override fun provideProvinces(
                    addressReceiver: AddressProvider.AddressReceiver<chihane.jdaddressselector.model.Province>?
                ) {

                    GlobalScope.launch {
                        val provinceList = dao.loadAllPrivinces()

                        val provinces = mutableListOf<chihane.jdaddressselector.model.Province>()
                        for (item in provinceList) {
                            val p = chihane.jdaddressselector.model.Province()
                            p.id = item.id
                            p.name = item.name
                            provinces.add(p)
                        }

                        runOnUiThread { addressReceiver?.send(provinces) }
                    }
                }

                override fun provideCountiesWith(
                    cityId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<chihane.jdaddressselector.model.County>?
                ) {
                    GlobalScope.launch {
                        val countyList = dao.loadCountiesByCityId(cityId)
                        val counties = mutableListOf<chihane.jdaddressselector.model.County>()
                        for (item in countyList) {
                            val c = chihane.jdaddressselector.model.County()
                            c.id = item.id
                            c.name = item.name
                            c.city_id = item.cityId
                            counties.add(c)
                        }

                        runOnUiThread { addressReceiver?.send(counties) }
                    }
                }

                override fun provideCitiesWith(
                    provinceId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<chihane.jdaddressselector.model.City>?
                ) {
                    GlobalScope.launch {
                        val cityList = dao.loadCitiesByProvinceId(provinceId)
                        val cities = mutableListOf<chihane.jdaddressselector.model.City>()
                        for (item in cityList) {
                            val c = chihane.jdaddressselector.model.City()
                            c.id = item.id
                            c.name = item.name
                            c.province_id = item.provinceId
                            cities.add(c)
                        }

                        runOnUiThread { addressReceiver?.send(cities) }
                    }
                }

                override fun provideStreetsWith(
                    countyId: Int,
                    addressReceiver: AddressProvider.AddressReceiver<chihane.jdaddressselector.model.Street>?
                ) {
                    GlobalScope.launch {
                        val streetList = dao.loadStreetsByCountyId(countyId)
                        val streets = mutableListOf<chihane.jdaddressselector.model.Street>()
                        for (item in streetList) {
                            val s = chihane.jdaddressselector.model.Street()
                            s.id = item.id
                            s.name = item.name
                            s.county_id = item.countyId
                            streets.add(s)
                        }

                        runOnUiThread { addressReceiver?.send(streets) }
                    }
                }

            }

            val listener = OnAddressSelectedListener { province, city, county, street ->
                addressButton.text = "${province.name}, ${city.name}, ${county?.name}, ${street?.name}"
            }

            runOnUiThread { BottomDialog.show(this@MainActivity, listener, provider) }
        }
    }

    /**
     * 判断所有地址是否插入数据库
     **/
    private fun isInserted(): Boolean {
        val lastStreet = dao.loadStreetsById(40136)
        if (lastStreet != null && lastStreet.size > 0) {
            return true
        }
        return false
    }

    /**
     * 把所有数据添加到数据库中
     **/
    private fun insertAll(dao: AddressDao) {
        GlobalScope.launch {
            // 查询是否已经加载
            if (isInserted()) {
                ToastUtils.showShort("has been inserted")
                return@launch
            }

            val assetManager = assets

            val provinces = transferProvince(getJsonArray(assetManager.open("province.json")))
            dao.insertProvinces(provinces)

            val cities = transferCity(getJsonArray(assetManager.open("city.json")))
            dao.insertCities(cities)

            val counties = transferCounty(getJsonArray(assetManager.open("county.json")))
            dao.insertCounties(counties)

            val streets = transferStreet(getJsonArray(assetManager.open("street.json")))
            dao.insertStreets(streets)

            runOnUiThread { ToastUtils.showShort("finished") }
        }
    }

    companion object {
        private fun readFile(input: InputStream): Reader {
            return BufferedReader(InputStreamReader(input))
        }

        val gson = Gson()

        private fun getJsonArray(input: InputStream): JsonArray {
            val jsonParser = JsonParser()
            return jsonParser.parse(readFile(input)).asJsonArray
        }

        private fun transferProvince(jsonArray: JsonArray): List<Province> {
            val provinces = mutableListOf<Province>()

            for (item in jsonArray) {
                provinces.add(gson.fromJson(item, Province::class.java))
            }

            return provinces
        }

        private fun transferCity(jsonArray: JsonArray): List<City> {
            val cities = mutableListOf<City>()

            for (item in jsonArray) {
                cities.add(gson.fromJson(item, City::class.java))
            }

            return cities
        }

        private fun transferCounty(jsonArray: JsonArray): List<County> {
            val counties = mutableListOf<County>()

            for (item in jsonArray) {
                counties.add(gson.fromJson(item, County::class.java))
            }

            return counties
        }

        private fun transferStreet(jsonArray: JsonArray): List<Street> {
            val streets = mutableListOf<Street>()

            for (item in jsonArray) {
                streets.add(gson.fromJson(item, Street::class.java))
            }

            return streets
        }
    }
}
