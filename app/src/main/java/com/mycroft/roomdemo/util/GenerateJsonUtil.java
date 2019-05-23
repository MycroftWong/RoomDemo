package com.mycroft.roomdemo.util;

import com.google.gson.Gson;
import com.mycroft.roomdemo.entity.City;
import com.mycroft.roomdemo.entity.County;
import com.mycroft.roomdemo.entity.Province;
import com.mycroft.roomdemo.entity.Street;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenerateJsonUtil {

    private static final String SQLITE_JDBC_CLASS_NAME = "org.sqlite.JDBC";

    private static final String DB_PATH = "jdbc:sqlite:.\\area.db";

    public static void main(String[] args) {

        try {
            connectSqlite();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String QUERY_PROVINCE = "SELECT * FROM Province";
    private static final String QUERY_CITY = "SELECT * FROM City";
    private static final String QUERY_COUNTY = "SELECT * FROM County";
    private static final String QUERY_STREET = "SELECT * FROM Street";

    private static void db2Json(Connection connection) throws SQLException, IOException {

        List<Province> provinces = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_PROVINCE);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");

            Province province = new Province(id, name);
            provinces.add(province);
        }

        writeFile(GSON.toJson(provinces), "./app/src/main/assets/province.json");

        List<City> cities = new ArrayList<>();
        preparedStatement = connection.prepareStatement(QUERY_CITY);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int provinceId = resultSet.getInt("province_id");

            City city = new City(id, provinceId, name);
            cities.add(city);
        }

        writeFile(GSON.toJson(cities), "./app/src/main/assets/city.json");

        List<County> counties = new ArrayList<>();
        preparedStatement = connection.prepareStatement(QUERY_COUNTY);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int cityId = resultSet.getInt("city_id");

            County county = new County(id, cityId, name);
            counties.add(county);
        }

        writeFile(GSON.toJson(counties), "./app/src/main/assets/county.json");

        List<Street> streets = new ArrayList<>();
        preparedStatement = connection.prepareStatement(QUERY_STREET);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int countyId = resultSet.getInt("county_id");

            Street street = new Street(id, countyId, name);
            streets.add(street);
        }

        writeFile(GSON.toJson(streets), "./app/src/main/assets/street.json");

    }

    private static void writeFile(String text, String filePath) throws IOException {
        try (FileOutputStream out = new FileOutputStream(filePath)) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
            writer.write(text);
            writer.flush();
        }
    }

    private static final Gson GSON = new Gson();


    private static void connectSqlite() throws SQLException, ClassNotFoundException, IOException {
        try (Connection connection = createConnection()) {
            db2Json(connection);

            connection.setAutoCommit(false);
            connection.commit();
        }
    }

    private static Connection createConnection() throws SQLException, ClassNotFoundException {
        Class.forName(SQLITE_JDBC_CLASS_NAME);
        return DriverManager.getConnection(DB_PATH);
    }

}
