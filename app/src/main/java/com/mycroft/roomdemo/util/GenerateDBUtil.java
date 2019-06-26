package com.mycroft.roomdemo.util;

import com.mycroft.roomdemo.entity.City;
import com.mycroft.roomdemo.entity.County;
import com.mycroft.roomdemo.entity.Province;
import com.mycroft.roomdemo.entity.Street;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 生成room可读取的db文件
 * <p>
 * 1. 找到数据，db, 或者json, 这里使用db文件
 * 2. 读取数据
 * 3. 构造可迁移的数据库(即生成Room可读取的schema的数据库)
 * 4. 写入数据
 */
public class GenerateDBUtil {


    private static final String SQLITE_JDBC_CLASS_NAME = "org.sqlite.JDBC";

    private static final String SOURCE_DB_PATH = "jdbc:sqlite:.\\area.db";
    private static final String DEST_DB_PATH = "jdbc:sqlite:.\\address.db";

    private static final String QUERY_PROVINCE = "SELECT * FROM Province";
    private static final String QUERY_CITY = "SELECT * FROM City";
    private static final String QUERY_COUNTY = "SELECT * FROM County";
    private static final String QUERY_STREET = "SELECT * FROM Street";


    private static final String CREATE_PROVINCE = "CREATE TABLE `province` (`id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`))";
    private static final String CREATE_CITY = "CREATE TABLE `city` (`id` INTEGER NOT NULL, `province_id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`province_id`) REFERENCES `province`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )";
    private static final String CREATE_COUNTY = "CREATE TABLE `county` (`id` INTEGER NOT NULL, `city_id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`city_id`) REFERENCES `city`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )";
    private static final String CREATE_STREET = "CREATE TABLE `street` (`id` INTEGER NOT NULL, `county_id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`), FOREIGN KEY(`county_id`) REFERENCES `county`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )";

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        migrateDB();
    }

    private static void migrateDB() throws SQLException, ClassNotFoundException {
        // 1. 找到数据，db, 或者json, 这里使用db文件
        try (Connection sourceConnection = createConnection(SOURCE_DB_PATH)) {
            // 2. 读取数据
            List<Province> provinces = readProvince(sourceConnection);
            List<City> cities = readCity(sourceConnection);
            List<County> counties = readCounty(sourceConnection);
            List<Street> streets = readStreet(sourceConnection);

            // 3.1 构造数据库
            try (Connection destConnection = createConnection(DEST_DB_PATH)) {
                // 3.2 生成Room可读取的schema
                createDB(destConnection);

                // 4. 写入数据
                writeData(destConnection, provinces, cities, counties, streets);
            }
        }
    }

    private static void writeData(Connection destConnection, List<Province> provinces, List<City> cities, List<County> counties, List<Street> streets) throws SQLException {
        destConnection.setAutoCommit(false);
        writeProvince(destConnection, provinces);
        destConnection.setAutoCommit(true);

        destConnection.setAutoCommit(false);
        writeCity(destConnection, cities);
        destConnection.setAutoCommit(true);

        destConnection.setAutoCommit(false);
        writeCounty(destConnection, counties);
        destConnection.setAutoCommit(true);

        destConnection.setAutoCommit(false);
        writeStreet(destConnection, streets);
        destConnection.setAutoCommit(true);
    }

    private static void writeProvince(Connection destConnection, List<Province> provinces) throws SQLException {
        PreparedStatement preparedStatement = destConnection.prepareStatement("INSERT INTO province values (?,?)");

        for (int i = 0; i < provinces.size(); i++) {
            Province province = provinces.get(i);
            preparedStatement.setInt(1, province.getId());
            preparedStatement.setString(2, province.getName());

            preparedStatement.addBatch();
            if ((i + 1) % 100 == 0) {
                int[] result = preparedStatement.executeBatch();
                System.out.println(result);
            }
        }
        int[] result = preparedStatement.executeBatch();
        System.out.println(result);

    }

    private static void writeCity(Connection destConnection, List<City> cities) throws SQLException {
        PreparedStatement preparedStatement = destConnection.prepareStatement("INSERT INTO city values (?,?,?)");
        for (int i = 0; i < cities.size(); i++) {
            City city = cities.get(i);
            preparedStatement.setInt(1, city.getId());
            preparedStatement.setInt(2, city.getProvinceId());
            preparedStatement.setString(3, city.getName());

            preparedStatement.addBatch();
            if ((i + 1) % 100 == 0) {
                int[] result = preparedStatement.executeBatch();
                System.out.println(result);
            }
        }
        int[] result = preparedStatement.executeBatch();
        System.out.println(result);
    }

    private static void writeCounty(Connection destConnection, List<County> counties) throws SQLException {
        PreparedStatement preparedStatement = destConnection.prepareStatement("INSERT INTO county values (?,?,?)");
        for (int i = 0; i < counties.size(); i++) {
            County county = counties.get(i);
            preparedStatement.setInt(1, county.getId());
            preparedStatement.setInt(2, county.getCityId());
            preparedStatement.setString(3, county.getName());

            preparedStatement.addBatch();
            if ((i + 1) % 100 == 0) {
                int[] result = preparedStatement.executeBatch();
                System.out.println(result);
            }
        }
        int[] result = preparedStatement.executeBatch();
        System.out.println(result);
    }

    private static void writeStreet(Connection destConnection, List<Street> streets) throws SQLException {
        PreparedStatement preparedStatement = destConnection.prepareStatement("INSERT INTO street values (?,?,?)");
        for (int i = 0; i < streets.size(); i++) {
            Street street = streets.get(i);
            preparedStatement.setInt(1, street.getId());
            preparedStatement.setInt(2, street.getCountyId());
            preparedStatement.setString(3, street.getName());

            preparedStatement.addBatch();
            if ((i + 1) % 100 == 0) {
                int[] result = preparedStatement.executeBatch();
                System.out.println(result);
            }
        }
        int[] result = preparedStatement.executeBatch();
        System.out.println(result);
    }


    private static Connection createConnection(String dbFilePath) throws ClassNotFoundException, SQLException {
        Class.forName(SQLITE_JDBC_CLASS_NAME);
        return DriverManager.getConnection(dbFilePath);
    }

    private static List<Province> readProvince(Connection connection) throws SQLException {
        List<Province> provinces = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_PROVINCE);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");

            Province province = new Province(id, name);
            provinces.add(province);
        }
        return provinces;
    }

    private static List<City> readCity(Connection connection) throws SQLException {
        List<City> cities = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_CITY);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int provinceId = resultSet.getInt("province_id");

            City city = new City(id, provinceId, name);
            cities.add(city);
        }
        return cities;
    }

    private static List<County> readCounty(Connection connection) throws SQLException {
        List<County> counties = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_COUNTY);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int cityId = resultSet.getInt("city_id");

            County county = new County(id, cityId, name);
            counties.add(county);
        }
        return counties;
    }

    private static List<Street> readStreet(Connection connection) throws SQLException {
        List<Street> streets = new ArrayList<>();
        PreparedStatement preparedStatement = connection.prepareStatement(QUERY_STREET);
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int countyId = resultSet.getInt("county_id");

            Street street = new Street(id, countyId, name);
            streets.add(street);
        }

        return streets;
    }

    private static void createDB(Connection destConnection) throws SQLException {
        Statement statement = destConnection.createStatement();
        statement.execute(CREATE_PROVINCE);
        statement.execute(CREATE_CITY);
        statement.execute(CREATE_COUNTY);
        statement.execute(CREATE_STREET);
    }

}

